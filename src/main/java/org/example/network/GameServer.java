package org.example.network;

import org.example.model.lobby_models.LobbyInvitation;
import org.example.model.message_center.Message;
import org.example.model.consts.Type;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.CancelledKeyException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.util.*;
import java.util.concurrent.*;

public class GameServer {

    private static final int PORT = 12346;
    private static final int TICK_RATE_MS = 100;

    private static boolean running = true;

    private static ServerSocketChannel serverChannel;
    private static Selector selector;

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private final ExecutorService workerPool = Executors.newFixedThreadPool(8);

    private final BlockingQueue<Message> globalRequestQueue = new LinkedBlockingQueue<>();
    private final BlockingQueue<LobbyInvitation> globalInvitationQueue = new LinkedBlockingQueue<>();

    private static ClientHandler clientHandler;

    private RequestProcessor requestProcessor;


    public void start() throws IOException {
        selector = Selector.open();
        serverChannel = ServerSocketChannel.open();
        serverChannel.bind(new InetSocketAddress(PORT));
        serverChannel.configureBlocking(false);
        serverChannel.register(selector, SelectionKey.OP_ACCEPT);

        clientHandler = new ClientHandler(selector, globalRequestQueue);
        requestProcessor = new RequestProcessor(clientHandler, globalRequestQueue);;

        // Start request processor thread
        requestProcessor.start();

//        scheduler.scheduleAtFixedRate(this::gameTick, 0, TICK_RATE_MS, TimeUnit.MILLISECONDS);

        System.out.println("Server started on port " + PORT);
        runSelectorPool();
    }

    public void runSelectorPool() throws IOException {
        while (running) {
            selector.select();

            Set<SelectionKey> selectionKeys = selector.selectedKeys();
            Iterator<SelectionKey> iter = selectionKeys.iterator();

            while (iter.hasNext()) {
                SelectionKey key = iter.next();
                iter.remove();

                try {
                    if (key.isAcceptable()) {
                        clientHandler.acceptConnection(serverChannel);
                    }
                    if (key.isReadable()) {
                        clientHandler.readFromClient(key);
                    }
                    if (key.isWritable()) {
                        clientHandler.writeToClient(key);
                    }
                } catch (IOException | CancelledKeyException e) {
                    // Disconnect and cleanup the client
                    closeConnection(key);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            clientHandler.getChannelToConnection().forEach((channel, connection) -> {
                if (!connection.getOutgoingChunks().isEmpty()) {
                    SelectionKey key = channel.keyFor(selector);
                    if (key != null && key.isValid()) {
                        key.interestOps(SelectionKey.OP_READ | SelectionKey.OP_WRITE);
                    }
                }
            });
        }
    }

    private void closeConnection(SelectionKey key) {
        try {
            key.cancel();
            var channel = (java.nio.channels.SocketChannel) key.channel();
            clientHandler.removeConnection(channel); // You must implement this
            channel.close();
            System.out.println("Client disconnected and cleaned up");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void gameTick() {
        clientHandler.getChannelToConnection().forEach((channel, client) -> {

            Message gameUpdate = new Message(Type.TICK, Map.of("time", System.currentTimeMillis()));
            client.send(gameUpdate);
        });
    }


    public void shutdown() {
        try {
            scheduler.shutdown();
            workerPool.shutdown();
            selector.close();
            serverChannel.close();
        } catch (IOException ignored) {
        }
    }

    public static ClientHandler getClientHandler() {
        return clientHandler;
    }
}

package org.example.network;

import org.example.model.LobbyInvitation;
import org.example.model.Message;
import org.example.model.Type;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.*;

public class GameServer {

    private static final int PORT = 8080;
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
    private InvitationProcessor invitationProcessor;


    public void start() throws IOException {
        selector = Selector.open();
        serverChannel = ServerSocketChannel.open();
        serverChannel.bind(new InetSocketAddress(PORT));
        serverChannel.configureBlocking(false);
        serverChannel.register(selector, SelectionKey.OP_ACCEPT);

        clientHandler = new ClientHandler(selector, globalRequestQueue);
        requestProcessor = new RequestProcessor(clientHandler, globalRequestQueue);
        invitationProcessor = new InvitationProcessor(clientHandler, globalInvitationQueue);

        // Start request processor thread
        requestProcessor.start();

        // Start invitation processor thread
        invitationProcessor.start();

        scheduler.scheduleAtFixedRate(this::gameTick, 0, TICK_RATE_MS, TimeUnit.MILLISECONDS);

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
                } catch (CancellationException ignored) {
                }
            }

            clientHandler.getChannelToConnection().forEach((channel, connection) -> {
                if (!connection.getOutgoingMessages().isEmpty()) {
                    SelectionKey key = channel.keyFor(selector);
                    if (key != null && key.isValid()) {
                        key.interestOps(SelectionKey.OP_READ | SelectionKey.OP_WRITE);
                    }
                }
            });
        }
    }

    public void gameTick() {
        clientHandler.getChannelToConnection().forEach((channel, client) -> {
            Message gameUpdate = new Message(Type.TICK, String.valueOf(System.currentTimeMillis()));
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

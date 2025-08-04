package org.example.network;

import org.example.model.Message;
import org.example.model.Type;
import org.example.utils.MessageParser;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

public class ClientHandler {
    private final Selector selector;
    private final Map<SocketChannel, ClientConnection> channelToConnection = new ConcurrentHashMap<>();
    private final Map<String, ClientConnection> usernameToConnection = new ConcurrentHashMap<>();
    private final BlockingQueue<Message> globalRequestQueue;

    public ClientHandler(Selector selector, BlockingQueue<Message> globalRequestQueue) {
        this.selector = selector;
        this.globalRequestQueue = globalRequestQueue;
    }

    public void acceptConnection(ServerSocketChannel serverChannel) throws IOException {
        SocketChannel clientChannel = serverChannel.accept();
        clientChannel.configureBlocking(false);
        clientChannel.register(selector, SelectionKey.OP_READ);
        channelToConnection.put(clientChannel, new ClientConnection(clientChannel));
        System.out.println("Accepted connection from " + clientChannel.getRemoteAddress());
    }

    public void readFromClient(SelectionKey key) {
        SocketChannel channel = (SocketChannel) key.channel();
        ByteBuffer buffer = ByteBuffer.allocate(1024);

        try {

            int bytesRead = channel.read(buffer);
            if (bytesRead == -1) {
                closeConnection(channel);
                return;
            }

            buffer.flip();
            String line = StandardCharsets.UTF_8.decode(buffer).toString().trim();
            Message incoming = MessageParser.fromJson(line);
            incoming.setSource(channel);

            globalRequestQueue.add(incoming);

        } catch (Exception e) {
            closeConnection(channel);
        }
    }

    public void writeToClient(SelectionKey key) {
        SocketChannel channel = (SocketChannel) key.channel();
        ClientConnection client = channelToConnection.get(channel);

        try {
            Queue<ByteBuffer> chunks = client.getOutgoingChunks();

            while (!chunks.isEmpty()) {
                ByteBuffer buffer = chunks.peek();
                channel.write(buffer);

                if (buffer.hasRemaining()) {
                    break;
                } else {
                    chunks.poll();
                }
            }

            if (chunks.isEmpty()) {
                key.interestOps(key.interestOps() & ~SelectionKey.OP_WRITE);
            }

        } catch (IOException e) {
            closeConnection(channel);
        }
    }

    public void queueMessage(SocketChannel channel, Message msg) {
        ClientConnection client = channelToConnection.get(channel);
        if (client == null) return;

        try {

            if (msg.getType() == Type.LOGIN) {
                boolean success = Objects.equals(msg.getFromPayload("status"), "success");
                if (success) {
                    String username = (String) msg.getFromPayload("username");
                    client.setUsername(username);

                    usernameToConnection.put(username, client);
                    channelToConnection.put(channel, client);
                }
            }

            String json = MessageParser.toJson(msg);

            // Log
            System.out.println(json + " to " + channel.getRemoteAddress());
            byte[] jsonBytes = json.getBytes(StandardCharsets.UTF_8);
            int length = jsonBytes.length;

            ByteBuffer header = ByteBuffer.allocate(4);
            header.putInt(length);
            header.flip();

            client.getOutgoingChunks().add(header);

            int chunkSize = 8 * 1024;
            for (int i = 0; i < jsonBytes.length; i += chunkSize) {
                int remaining = Math.min(chunkSize, jsonBytes.length - i);
                ByteBuffer chunk = ByteBuffer.wrap(jsonBytes, i, remaining);
                client.getOutgoingChunks().add(chunk);
            }

            SelectionKey key = channel.keyFor(selector);
            if (key != null) {
                key.interestOps(key.interestOps() | SelectionKey.OP_WRITE);
                selector.wakeup();
            }

        } catch (Exception e) {
            e.printStackTrace();
            closeConnection(channel);
        }
    }

    public void closeConnection(SocketChannel channel) {
        try {
            System.out.println("Closing connection: " + channel.getRemoteAddress());
            ClientConnection conn = channelToConnection.remove(channel);
            if (conn != null && conn.getUsername() != null) {
                usernameToConnection.remove(conn.getUsername());
            }
            channel.close();
        } catch (IOException ignored) {}
    }

    public ClientConnection getClientByChannel(SocketChannel channel) {

        return channelToConnection.get(channel);
    }

    public ClientConnection getClientByUsername(String username) {
        return usernameToConnection.get(username);
    }


    public void onUserAuthenticated(String username, SocketChannel channel) {
        ClientConnection conn = channelToConnection.get(channel);
        if (conn != null) {
            conn.setUsername(username);
            usernameToConnection.put(username, conn);
        }
    }

    public Collection<ClientConnection> getClients() {
        return channelToConnection.values();
    }

    public Map<String, ClientConnection> getUsernameToConnection() {
        return usernameToConnection;
    }

    public Map<SocketChannel, ClientConnection> getChannelToConnection() {
        return channelToConnection;
    }

    public void removeConnection(SocketChannel channel) {
        channelToConnection.remove(channel);
    }

    public boolean isUserOnline(String username) {
        return usernameToConnection.containsKey(username);
    }
}

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

        Queue<Message> queue = client.getOutgoingMessages();
        try {
            while (!queue.isEmpty()) {
                // Poll message
                Message msg = queue.poll();

                // Store user if login successes
                Type msgType = msg.getType();
                if (msgType == Type.LOGIN) {
                    boolean success = Objects.equals(msg.getFromPayload("status"), "success");
                    if (success) {
                        String username = (String) msg.getFromPayload("username");
                        onUserAuthenticated(username, channel);
                    }
                }

                // Send message to user
                send(channel, msg);
            }
        } catch (Exception e) {
            closeConnection(channel);
        }
    }

    public void send(SocketChannel channel, Message msg) {
        try {
            String json = MessageParser.toJson(msg);

            // Log
            System.out.println("sending " + "\""+ json + "\"" + "to " + channel.getRemoteAddress());
            byte[] jsonBytes = json.getBytes(StandardCharsets.UTF_8);
            int length = jsonBytes.length;

            ByteBuffer buffer = ByteBuffer.allocate(4 + length);
            buffer.putInt(length);
            buffer.put(jsonBytes);
            buffer.flip();

            channel.write(buffer);
        } catch (Exception e) {
            e.printStackTrace();
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

    public Map<SocketChannel, ClientConnection> getChannelToConnection() {
        return channelToConnection;
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

    public void removeConnection(SocketChannel channel) {
        channelToConnection.remove(channel);
    }

    public boolean isUserOnline(String username) {
        return usernameToConnection.containsKey(username);
    }
}

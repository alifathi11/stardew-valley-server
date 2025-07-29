package org.example.network;

import org.example.model.Message;
import org.example.utils.MessageParser;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Queue;
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
                Message msg = queue.poll();
                String json = MessageParser.toJson(msg) + "\n";
                ByteBuffer buffer = ByteBuffer.wrap(json.getBytes(StandardCharsets.UTF_8));
                channel.write(buffer);
            }
        } catch (Exception e) {
            closeConnection(channel);
        }
    }

    public void closeConnection(SocketChannel channel) {
        try {
            System.out.println("Closing connection: " + channel.getRemoteAddress());
            channelToConnection.remove(channel);
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
}

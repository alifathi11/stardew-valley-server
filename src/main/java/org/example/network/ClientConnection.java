package org.example.network;

import com.badlogic.gdx.graphics.Mesh;
import org.example.model.Message;
import org.example.model.Type;
import org.example.utils.MessageParser;

import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class ClientConnection {

    private GameSession gameSession;
    private final SocketChannel channel;
    private final Queue<Message> outgoingMessages = new ConcurrentLinkedQueue<>();
    private final UUID id = UUID.randomUUID();
    private String username;

    private final Queue<ByteBuffer> outgoingChunks = new ConcurrentLinkedQueue<>();


    public ClientConnection(SocketChannel channel) {
        this.channel = channel;
    }

    public void send(Message msg) {
        GameServer.getClientHandler().queueMessage(channel, msg);
    }

    public Queue<Message> getOutgoingMessages() {
        return outgoingMessages;
    }

    public SocketChannel getChannel() {
        return channel;
    }

    public UUID getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setGameSession(GameSession gameSession) {
        this.gameSession = gameSession;
    }

    public GameSession getGameSession() {
        return gameSession;
    }

    public Queue<ByteBuffer> getOutgoingChunks() {
        return outgoingChunks;
    }
}
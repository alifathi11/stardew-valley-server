package org.example.network;

import com.badlogic.gdx.graphics.Mesh;
import org.example.model.Message;

import java.net.Socket;
import java.nio.channels.SocketChannel;
import java.util.Map;
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

    public ClientConnection(SocketChannel channel) {
        this.channel = channel;
    }

    public void send(Message msg) {
        outgoingMessages.offer(msg);
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
}
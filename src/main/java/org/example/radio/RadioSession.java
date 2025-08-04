package org.example.radio;

import org.example.model.game_models.Game;
import org.example.network.GameServer;

import java.nio.channels.SocketChannel;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class RadioSession {
    private final String sessionId;
    private final Set<SocketChannel> listeners = ConcurrentHashMap.newKeySet();
    private final Deque<AudioChunk> buffer = new ArrayDeque<>();

    private static final int MAX_BUFFER_SIZE = 100;

    public RadioSession(String sessionId) {
        this.sessionId = sessionId;
    }

    public void broadcast(AudioChunk chunk) {
        buffer.addLast(chunk);
        if (buffer.size() > MAX_BUFFER_SIZE) {
            buffer.removeFirst();
        }

        for (SocketChannel listener : listeners) {
            GameServer.getClientHandler().getClientByChannel(listener).send(chunk.toMessage());
        }
    }

    public void addListener(SocketChannel channel) {
        listeners.add(channel);
    }

    public void removeListener(SocketChannel channel) {
        listeners.remove(channel);
    }

    public Set<SocketChannel> getListeners() {
        return listeners;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void clear() {
        listeners.clear();
        buffer.clear();
    }
}

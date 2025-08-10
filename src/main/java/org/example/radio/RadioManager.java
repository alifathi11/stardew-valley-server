package org.example.radio;

import org.example.network.GameServer;

import java.net.Socket;
import java.nio.channels.SocketChannel;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
public class RadioManager {

    private final Map<String, RadioSession> sessions = new ConcurrentHashMap<>();
    private final Map<String, String> userToSession = new ConcurrentHashMap<>();
    private Map<String, Object> format;

    public void createSession(String hostId, Map<String, Object> format) {
        sessions.putIfAbsent(hostId, new RadioSession(hostId, format));

        // Log
        System.err.println("host " + hostId + " started broadcasting");
    }

    public void createOrUpdateSession(String hostId, AudioChunk chunk) {
        RadioSession session = sessions.get(hostId);
        if (session == null) {
            session = new RadioSession(hostId, format);
            sessions.put(hostId, session);
        }
        session.broadcast(chunk);

    }

    public boolean addListener(String listenerUsername, String sessionId, SocketChannel channel) {
        var session = sessions.get(sessionId);
        if (session == null) return false;

        session.addListener(channel);
        userToSession.put(listenerUsername, sessionId);
        return true;
    }

    public boolean removeListener(String listenerUsername, SocketChannel channel) {
        String sessionId = userToSession.remove(listenerUsername);
        if (sessionId == null) return false;

        var session = sessions.get(sessionId);
        if (session == null) return false;

        session.removeListener(channel);
        return true;
    }

    public void stopSession(String hostId) {
        RadioSession session = sessions.remove(hostId);
        if (session == null) return;

        userToSession.values().removeIf(s -> s.equals(hostId));

        session.clear();
    }

    public Map<String, String> getUserToSession() {
        return userToSession;
    }

    public Map<String, RadioSession> getSessions() {
        return sessions;
    }
}

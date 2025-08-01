package org.example.network;

import org.example.model.Game;
import org.example.model.Message;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class GameSession {
    private final String sessionId;
    private Game game;
    private final Map<String, ClientConnection> players = new ConcurrentHashMap<>();

    public GameSession(String sessionId) {
        this.sessionId = sessionId;
    }

    public void addPlayer(String username, ClientConnection client) {
        players.put(username, client);
        client.setGameSession(this);
    }

    public void broadcast(Message message, String exceptUsername) {
        for (var entry : players.entrySet()) {
            if (!entry.getKey().equalsIgnoreCase(exceptUsername)) {
                entry.getValue().send(message);
            }
        }
    }

    public Collection<ClientConnection> getPlayers() {
        return players.values();
    }

    public void setGame(Game game) {
        this.game = game;
    }
}

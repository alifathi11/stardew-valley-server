package org.example.model;

import java.util.List;

public class Game {
    private final String id;
    private List<Player> players;

    public Game(String id,
                List<Player> players) {
        this.id = id;
        this.players = players;
    }

    public String getId() {
        return id;
    }

    public List<Player> getPlayers() {
        return players;
    }
}

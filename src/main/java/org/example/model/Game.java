package org.example.model;

import java.util.*;

public class Game {
    private final Set<Player> players;

    public Game(Set<String> users,
                Map<String, String> playerNames,
                Map<String, Gender> playerGenders) {
        this.players = buildPlayers(users, playerNames, playerGenders);
    }

    private Set<Player> buildPlayers(Set<String> users,
                                     Map<String, String> playerNames,
                                     Map<String, Gender> playerGenders) {
        Set<Player> players = new HashSet<>();
        for (String user : users) {
            players.add(new Player(user,
                                   playerNames.get(user),
                                   playerGenders.get(user)));
        }

        return players;
    }


    public Set<Player> getPlayers() {
        return players;
    }
}

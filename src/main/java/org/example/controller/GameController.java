package org.example.controller;

import org.example.model.Game;

import java.util.List;
import java.util.Map;

public class GameController {
    private static Map<String, Game> games;

    public static void addGame(Game game) {
        games.put(game.getId(), game);
    }

    public static Game getGame(String id) {
        return games.get(id);
    }

    public static Map<String, Game> getGames() {
        return games;
    }
}

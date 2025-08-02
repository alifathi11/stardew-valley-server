package org.example.model;

import org.example.network.ClientConnection;
import org.example.network.GameServer;
import org.example.network.GameSession;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Game {
    private final Set<Player> players;
    private final Set<NPC> npcs;
    private final Set<Animal> animals;
    private final ScheduledExecutorService executor;

    public Game(Set<String> users,
                Map<String, String> playerNames,
                Map<String, Gender> playerGenders) {

        this.players = buildPlayers(users, playerNames, playerGenders);
        this.executor = Executors.newScheduledThreadPool(1);

        executor.scheduleAtFixedRate(() -> {
           broadcastEntityUpdates();
        }, 0, 100, TimeUnit.MILLISECONDS);
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

    private void broadcastEntityUpdates() {
        for (Player player : players) {
            ClientConnection client = GameServer.getClientHandler().getClientByUsername(player.getUsername());
            client.send(new Message(Type.ENTITY_UPDATE, buildEntityUpdateFor(player)));
        }
    }

    private Map<String, Object> buildEntityUpdateFor(Player player) {
        List<Map<String, Object>> playerPayload = new ArrayList<>();
        for (Player p : players) {
            if (p == player) continue;
            Map<String, Object> payload = new HashMap<>();
            payload.put("id", p.getUsername());
            payload.put("pos_x", p.getPosition().x);
            payload.put("pos_y", p.getPosition().y);

            playerPayload.add(payload);
        }

        List<Map<String, Object>> npcPayload = new ArrayList<>();
        for (NPC npc : npcs) {
            Map<String, Object> payload = new HashMap<>();
            payload.put("id", npc.getName());
            payload.put("pos_x", npc.getPosition().x);
            payload.put("pos_y", npc.getPosition().y);

            npcPayload.add(payload);
        }

        List<Map<String, Object>> animalPayload = new ArrayList<>();
        for (Animal animal : animals) {
            Map<String, Object> payload = new HashMap<>();
            payload.put("id", animal.getId());
            payload.put("pos_x", animal.getPosition().x);
            payload.put("pos_y", animal.getPositino().y);

            animalPayload.add(payload);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("players", playerPayload);
        result.put("npcs", npcPayload);
        result.put("animals", animalPayload);

        return result;
    }
}

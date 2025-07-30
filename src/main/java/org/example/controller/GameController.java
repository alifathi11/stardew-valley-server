package org.example.controller;

import com.badlogic.gdx.maps.MapObject;
import org.aspectj.apache.bcel.generic.InstructionConstants;
import org.example.global.LobbyManager;
import org.example.model.*;
import org.example.network.ClientConnection;
import org.example.network.GameServer;
import org.example.network.GameSession;
import org.hibernate.sql.results.internal.StandardEntityGraphTraversalStateImpl;

import java.awt.event.MouseWheelEvent;
import java.security.SecureRandom;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class GameController {
    public Message createGame(Message message) {
        String username = (String) message.getFromPayload("username");
        String lobbyId = (String) message.getFromPayload("lobby_id");

        Lobby lobby = LobbyManager.getLobby(lobbyId);
        if (lobby == null) {
            return Message.error(Type.CREATE_GAME, "Lobby doesn't exist.");
        }

        Set<String> members = lobby.getMembers();

        if (!lobby.getHostUsername().equalsIgnoreCase(username)) {
            return Message.error(Type.CREATE_GAME, "Only host can start the game.");
        }

        if (lobby.getMembers().size() < 2) {
            return Message.error(Type.CREATE_GAME, "Lobby must have at least 2 members to start the game.");
        }


        // Create game session
        GameSession session = new GameSession(UUID.randomUUID().toString());
        lobby.setSession(session);

        for (String member : members) {
            ClientConnection client = GameServer.getClientHandler().getClientByUsername(member);
            if (client == null) continue;
            session.addPlayer(member, client);
        }

        // Send create game signal for lobby members
        Map<String, Object> payload = new ConcurrentHashMap<>();
        payload.put("lobby_id", lobbyId);

        Message response = new Message(Type.CREATE_GAME, payload);

        for (String member : members) {
            ClientConnection conn = GameServer.getClientHandler().getClientByUsername(member);
            conn.send(response);
        }

        return Message.success(Type.CREATE_GAME, "Game created");
    }

    public Message chooseMap(Message message) {
        String username = (String) message.getFromPayload("username");
        String lobbyId = (String) message.getFromPayload("lobby_id");
        int mapNumber = (Integer) message.getFromPayload("map_number");

        Message response = LobbyManager.chooseMap(lobbyId, username, mapNumber);
        if (Objects.equals(response.getFromPayload("status"), "error")) {
            return response;
        }

        Map<String, Object> payload = new HashMap<>();
        payload.put("status", "success");
        payload.put("map_number", mapNumber);
        payload.put("lobby_id", lobbyId);

        return new Message(Type.CHOOSE_MAP, payload);
    }

    public Message playerMove(Message message) {
        String username = (String) message.getFromPayload("username");
        ClientConnection client = GameServer.getClientHandler().getClientByUsername(username);
        if (client != null) {
            GameSession session = client.getGameSession();
            if (session != null) {
                session.broadcast(message, username);
            }
        }

        return Message.success(Type.PLAYER_MOVE, "you have been updated successfully.");
    }

}

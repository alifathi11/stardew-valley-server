package org.example.global;

import org.example.model.*;
import org.example.network.ClientConnection;
import org.example.network.GameServer;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class LobbyManager {
    private static final Map<String, Lobby> lobbies = new ConcurrentHashMap<>();
    private static final Map<String, String> userToLobbyId = new ConcurrentHashMap<>();

    public static Lobby createLobby(String hostUsername,
                                    boolean isPrivate,
                                    boolean isVisible,
                                    String password) {

        Lobby lobby = new Lobby(UUID.randomUUID().toString(),
                                hostUsername,
                                isPrivate,
                                isVisible,
                                password);

        lobbies.put(lobby.getId(), lobby);
        userToLobbyId.put(hostUsername, lobby.getId());
        return lobby;
    }

    public static boolean joinLobby(String username, String lobbyId) {
        Lobby lobby = lobbies.get(lobbyId);
        if (lobby == null || lobby.getState() != LobbyState.WAITING) {
            return false;
        }

        if (lobby.getMembers().size() >= 4) {
            return false;
        }

        lobby.addMember(username);
        userToLobbyId.put(username, lobbyId);
        return true;
    }

    public static boolean leaveLobby(String username) {
        String lobbyId = userToLobbyId.get(username);
        if (lobbyId == null) return false;

        Lobby lobby = lobbies.get(lobbyId);
        if (lobby == null) return false;

        lobby.removeMember(username);
        userToLobbyId.remove(username);
        if (lobby.getMembers().isEmpty()) {
            lobbies.remove(lobbyId);
        }

        return true;
    }

    public static Lobby getLobbyByUser(String username) {
        String lobbyId = userToLobbyId.get(username);
        return lobbyId == null ? null : lobbies.get(lobbyId);
    }

    public static Lobby getLobby(String lobbyId) {
        return lobbies.get(lobbyId);
    }

    public static Collection<Lobby> getLobbies() {
        return lobbies.values();
    }

    public static Message chooseMap(String lobbyId, String username, int number) {

        Lobby lobby = lobbies.get(lobbyId);
        if (lobby == null) {
            return Message.error(Type.CHOOSE_MAP, "Lobby doesn't exist");
        }


        if (number > 4 || number < 1) {
            return Message.error(Type.CHOOSE_MAP, "Invalid argument.");
        }

        var userToMapNumber = lobby.getUserToMapNumber();

        if (userToMapNumber.containsValue(number)) {
            return Message.error(Type.CHOOSE_MAP, "Map is already chosen.");
        }

        userToMapNumber.put(username, number);

        if (userToMapNumber.size() == lobby.getMembers().size()) {
            startGame(lobbyId);
        }

        return Message.success(Type.CHOOSE_MAP, "Map successfully selected.");

    }

    public static void startGame(String lobbyId) {

        Lobby lobby = lobbies.get(lobbyId);

        if (lobby == null) return;

        Map<String, Object> payload = new HashMap<>();
        payload.put("lobby_id", lobby);

        for (String member : lobby.getMembers()) {
            Message message = new Message(Type.START_GAME, payload);
            ClientConnection client = GameServer.getClientHandler().getClientByUsername(member);
            client.send(message);
        }

        lobby.setState(LobbyState.IN_GAME);

        Game game = new Game(lobby.getMembers(), lobby.getPlayerNames(), lobby.getPlayerGenders());
        lobby.getSession().setGame(game);
    }
}

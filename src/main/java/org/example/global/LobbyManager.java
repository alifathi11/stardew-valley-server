package org.example.global;

import org.example.model.*;
import org.example.network.ClientConnection;
import org.example.network.GameServer;

import java.sql.Array;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class LobbyManager {
    private static final Map<String, Lobby> lobbies = new ConcurrentHashMap<>();
    private static final Map<String, String> userToLobbyId = new ConcurrentHashMap<>();

    public static Lobby createLobby(String lobbyName,
                                    String hostUsername,
                                    boolean isPrivate,
                                    boolean isVisible,
                                    String password) {

        Lobby lobby = new Lobby(UUID.randomUUID().toString(),
                                lobbyName,
                                hostUsername,
                                isPrivate,
                                isVisible,
                                password);

        lobby.addMember(hostUsername);

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

    public static boolean leaveLobby(String username, String lobbyId) {

        Lobby lobby = lobbies.get(lobbyId);
        if (lobby == null) return false;

        lobby.removeMember(username);
        userToLobbyId.remove(username);
        if (lobby.getMembers().isEmpty()) {
            lobbies.remove(lobbyId);
        }

        // Change the host of the lobby if user was host
        if (!lobby.getMembers().isEmpty() &&
             lobby.getHostUsername().equalsIgnoreCase(username)) {

            String newHost = new ArrayList<>(lobby.getMembers()).get(0);
            lobby.setHostUsername(newHost);
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

        if (userToMapNumber.containsKey(username)) {
            return Message.error(Type.CHOOSE_MAP, "You have already chosen a map.");
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

        lobby.setState(LobbyState.IN_GAME);

        Game game = new Game(lobby.getMembers(), lobby.getPlayerNames(), lobby.getPlayerGenders());
        lobby.getSession().setGame(game);

        Message message = buildStartGameMessage(game);

        for (String member : lobby.getMembers()) {
            ClientConnection client = GameServer.getClientHandler().getClientByUsername(member);
            client.send(message);
        }
    }

    private Message buildStartGameMessage(Game game) {
        Map<String, Object> mapPayload = new HashMap<>();
        mapPayload.put("width", mapData.getWith());
        mapPayload.put("height", mapData.getHeight());
        mapPayload.put("data", mapData.getTiles());

        List<Map<String, Object>> playerPayload = new ArrayList<>();
        for (Player player : game.getPlayers()) {
            Map<String, Object> pMap = new HashMap<>();
            pMap.put("");
        }
    }
}

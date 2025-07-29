package org.example.global;

import org.example.model.Lobby;
import org.example.model.LobbyState;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class LobbyManager {
    private static final Map<String, Lobby> lobbies = new ConcurrentHashMap<>();
    private static final Map<String, String> userToLobbyId = new ConcurrentHashMap<>();

    public static Lobby createLobby(String hostUsername,
                                    boolean isPrivate,
                                    boolean isVisible,
                                    String password) {

        Lobby lobby = new Lobby(UUID.randomUUID().toString(),
                                hostUsername,
                                ConcurrentHashMap.newKeySet(),
                                LobbyState.WAITING,
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
}

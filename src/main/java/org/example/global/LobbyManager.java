package org.example.global;

import org.example.model.consts.LobbyState;
import org.example.model.consts.MapSize;
import org.example.model.consts.Type;
import org.example.model.game_models.Game;
import org.example.model.game_models.NPC;
import org.example.model.game_models.Player;
import org.example.model.game_models.Shop;
import org.example.model.lobby_models.Lobby;
import org.example.model.message_center.Message;
import org.example.network.ClientConnection;
import org.example.network.GameServer;
import org.hibernate.exception.spi.TemplatedViolatedConstraintNameExtractor;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

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

    public static void deleteLobby(Lobby lobby) {
        lobbies.remove(lobby.getId());
        for (var entry : userToLobbyId.entrySet()) {
            if (entry.getValue().equalsIgnoreCase(lobby.getId())) {
                userToLobbyId.remove(entry.getKey(), entry.getValue());
            }
        }
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

        if (userToMapNumber.size() >= lobby.getMembers().size()) {
            startGame(lobbyId);
        }

        return Message.success(Type.CHOOSE_MAP, "Map successfully selected.");

    }

    public static void startGame(String lobbyId) {

        Lobby lobby = lobbies.get(lobbyId);

        if (lobby == null) return;

        lobby.setState(LobbyState.IN_GAME);

        Game game = new Game(UUID.randomUUID().toString(), lobby.getSession(), lobby);

        lobby.getSession().setGame(game);
        Message message = buildStartGameMessage(game);

        for (String member : lobby.getMembers()) {
            ClientConnection client = GameServer.getClientHandler().getClientByUsername(member);
            client.send(message);
        }
    }

    private static Message buildStartGameMessage(Game game) {

        List<Map<String, Object>> playerPayload = new ArrayList<>();
        for (Player player : game.getPlayers()) {
            Map<String, Object> payload = new HashMap<>();
            payload.put("id", player.getId());
            payload.put("username", player.getUsername());
            payload.put("name", player.getName());
            payload.put("gender", player.getGender());
            payload.put("pos_x", player.getPosition().x);
            payload.put("pos_y", player.getPosition().y);

            playerPayload.add(payload);
        }

        List<Map<String, Object>> NPCPayload = new ArrayList<>();
        for (NPC npc : game.getNPCs()) {
            Map<String, Object> payload = new HashMap<>();
            payload.put("id", npc.getId());
            payload.put("name", npc.getName());
            payload.put("pos_x", npc.getPosition().x);
            payload.put("pos_y", npc.getPosition().y);

            NPCPayload.add(payload);
        }

        List<Map<String, Object>> shopPayload = new ArrayList<>();
        for (Shop shop : game.getShops().values()) {
            Map<String, Object> payload = new HashMap<>();
            payload.put("id", shop.getId());
            payload.put("shop_name", shop.getShopName());
            payload.put("npc_name", shop.getOwner().getName());

            shopPayload.add(payload);
        }

//        String[][] map = new String[MapSize.MAP_WIDTH.getSize()][MapSize.MAP_HEIGHT.getSize()];
//        for (int x = 0; x < MapSize.MAP_WIDTH.getSize(); x++) {
//            for (int y = 0; y < MapSize.MAP_HEIGHT.getSize(); y++) {
//                map[x][y] = game.getMap().getTile(x, y).getId().name().toLowerCase();
//            }
//        }


        Map<String, Object> payload = new HashMap<>();
        payload.put("players", playerPayload);
        payload.put("npcs", NPCPayload);
        payload.put("shops", shopPayload);

        return new Message(Type.START_GAME, payload);
    }
}
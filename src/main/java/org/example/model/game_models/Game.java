package org.example.model.game_models;

import com.badlogic.gdx.math.Vector2;
import org.example.data.InitialPositionLoader;
import org.example.data.NPCLoader;
import org.example.data.QuestLoader;
import org.example.model.consts.Gender;
import org.example.model.consts.MapSize;
import org.example.model.consts.Type;
import org.example.model.lobby_models.Lobby;
import org.example.model.message_center.Message;
import org.example.network.ClientConnection;
import org.example.network.GameServer;
import org.example.model.game_models.PlayerAbilities.Ability;
import org.example.radio.RadioManager;

import java.io.File;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Game {
    private final String id;
    private final Set<String> users;
    private final Map<String, String> playerNames;
    private final Map<String, Gender> playerGenders;
    private final Map<String, Integer> userToMapNumber;
    private final Map<Integer, Vector2> mapNumberToPosition;
    private Map<Player, PlayerMap> playerToPlayerMap;
    private List<Chat> chats;

    private List<Player> players;
    private List<NPC> npcs;
    private Map<String, List<Quest>> questByNpc;
    private Set<Animal> animals;

    private GameMap map;

    private final RadioManager radioManager;

    private final ScheduledExecutorService leaderboardExecutor;

    public Game(String id, Lobby lobby) {

        this.id = id;

        this.users = lobby.getMembers();
        this.playerNames = lobby.getPlayerNames();
        this.playerGenders = lobby.getPlayerGenders();
        this.userToMapNumber = lobby.getUserToMapNumber();

        this.animals = new HashSet<>();
        this.questByNpc = new HashMap<>();
        this.npcs = new ArrayList<>();
        this.players = new ArrayList<>();
        this.playerToPlayerMap = new HashMap<>();
        this.mapNumberToPosition = new HashMap<>();
        this.chats = new ArrayList<>();


        this.leaderboardExecutor = Executors.newScheduledThreadPool(1);

        leaderboardExecutor.scheduleAtFixedRate(this::broadcastLeaderboardUpdates, 0, 1000, TimeUnit.MILLISECONDS);

        this.radioManager = new RadioManager();

        initialize();

    }



    private void initialize() {
        // Build map entities
        buildPlayers();
        buildNPCs();
        buildQuests();

        // Build map
        buildMap();

        // Build player maps
        buildPlayerMaps();

        // Build chats
        buildChats();
    }

    private void buildChats() {
        for (int i = 0; i < players.size(); i++) {
            for (int j = i + 1; j < players.size(); j++) {
                Player p1 = players.get(i);
                Player p2 = players.get(j);

                Chat chat = new Chat(p1, p2);
                chats.add(chat);
            }
        }
    }

    public void sendMessage(String fromUser, String toUser, Message message) {
        Chat chat = getChat(fromUser, toUser);
        chat.addMessage(message);
    }

    public List<Message> getMessage(String user1, String user2) {
        return getChat(user1, user2).getMessages();
    }

    private Chat getChat(String fromUser, String toUser) {
        return chats.stream().filter(c -> (c.getFirst().getUsername().equalsIgnoreCase(fromUser) ||
                                                 c.getFirst().getUsername().equalsIgnoreCase(toUser))  &&
                                                (c.getSecond().getUsername().equalsIgnoreCase(fromUser) ||
                                                 c.getSecond().getUsername().equalsIgnoreCase(toUser)))
                                                .toList().getFirst();
    }


    private void buildMap() {
        GameMap gameMap = new GameMap();
        gameMap.build();

        this.map = gameMap;
    }

    private void buildPlayerMaps() {

        Tile[][] map1 = new Tile[MapSize.PLAYER_MAP_WIDTH.getSize()][MapSize.PLAYER_MAP_HEIGHT.getSize()];
        for (int x = 0; x < MapSize.PLAYER_MAP_WIDTH.getSize(); x++) {
            for (int y = MapSize.MAP_HEIGHT.getSize() - MapSize.PLAYER_MAP_HEIGHT.getSize(); y < MapSize.MAP_HEIGHT.getSize(); y++) {
                map1[x][y - MapSize.MAP_HEIGHT.getSize() + MapSize.PLAYER_MAP_HEIGHT.getSize()] = map.getTile(x, y);
            }
        }

        Tile[][] map2 = new Tile[MapSize.PLAYER_MAP_WIDTH.getSize()][MapSize.PLAYER_MAP_HEIGHT.getSize()];
        for (int x = MapSize.MAP_WIDTH.getSize() - MapSize.PLAYER_MAP_WIDTH.getSize(); x < MapSize.MAP_WIDTH.getSize(); x++) {
            for (int y = MapSize.MAP_HEIGHT.getSize() - MapSize.PLAYER_MAP_HEIGHT.getSize(); y < MapSize.MAP_HEIGHT.getSize(); y++) {
                map2[x - MapSize.MAP_WIDTH.getSize() + MapSize.PLAYER_MAP_WIDTH.getSize()]
                        [y - MapSize.MAP_HEIGHT.getSize() + MapSize.PLAYER_MAP_HEIGHT.getSize()]
                        = map.getTile(x, y);
            }
        }

        Tile[][] map3 = new Tile[MapSize.PLAYER_MAP_WIDTH.getSize()][MapSize.PLAYER_MAP_HEIGHT.getSize()];
        for (int x = 0; x < MapSize.PLAYER_MAP_WIDTH.getSize(); x++) {
            for (int y = 0; y < MapSize.PLAYER_MAP_HEIGHT.getSize(); y++) {
                map3[x][y] = map.getTile(x, y);
            }
        }


        Tile[][] map4 = new Tile[MapSize.PLAYER_MAP_WIDTH.getSize()][MapSize.PLAYER_MAP_HEIGHT.getSize()];
        for (int x = MapSize.MAP_WIDTH.getSize() - MapSize.PLAYER_MAP_WIDTH.getSize(); x < MapSize.MAP_WIDTH.getSize(); x++) {
            for (int y = 0; y < MapSize.PLAYER_MAP_HEIGHT.getSize(); y++) {
                map4[x - MapSize.MAP_WIDTH.getSize() + MapSize.PLAYER_MAP_WIDTH.getSize()][y] = map.getTile(x, y);
            }
        }


        List<Tile[][]> playerMaps = new ArrayList<>();
        playerMaps.add(map1);
        playerMaps.add(map2);
        playerMaps.add(map3);
        playerMaps.add(map4);

        for(Player player : players) {
            int number = userToMapNumber.get(player.getUsername());
            PlayerMap playerMap = new PlayerMap(playerMaps.get(number - 1));

            playerToPlayerMap.put(player, playerMap);
            player.setMap(playerMap);
        }

    }

    private void buildPlayers() {

        List<Player> players = new ArrayList<>();
        Map<Integer, Vector2> mapNumberToPosition = InitialPositionLoader.loadMapPositions(new File("src/main/java/org/example/data/initialPositions.json"));

        for (String user : users) {
            players.add(new Player(UUID.randomUUID().toString(),
                                   user,
                                   playerNames.get(user),
                                   playerGenders.get(user),
                                   mapNumberToPosition.get(userToMapNumber.get(user))));
        }

        this.players = players;
    }

    private void buildNPCs() {
        File file = new File("src/main/java/org/example/data/npc.json");
        this.npcs = NPCLoader.loadNPCsFromFile(file);
    }

    private void buildQuests() {
        File file = new File("src/main/java/org/example/data/quests.json");
        this.questByNpc = QuestLoader.loadQuestsFromFile(file);

        for (var entry : questByNpc.entrySet()) {
            String name = entry.getKey();

            NPC npc = npcs.stream()
                    .filter(n -> Objects.equals(n.getName(), name))
                    .findFirst()
                    .orElse(null);

            if (npc != null) {
                npc.setQuests(entry.getValue());
            } else {
                System.err.println("NPC not found for name: " + name);
            }
        }

    }

    public List<Player> getPlayers() {
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
            payload.put("pos_y", animal.getPosition().y);

            animalPayload.add(payload);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("players", playerPayload);
        result.put("npcs", npcPayload);
        result.put("animals", animalPayload);

        return result;
    }

    public List<NPC> getNPCs() {
        return npcs;
    }

    public GameMap getMap() {
        return map;
    }

    private void broadcastLeaderboardUpdates() {
        try {

            List<Map<String, Object>> leaderboard = new ArrayList<>();

            for (Player player : players) {
                Map<String, Object> payload = new HashMap<>();

                payload.put("username", player.getUsername());
                payload.put("coin", player.getWallet().getCoin());
                payload.put("finished_quests", player.getQuests().stream()
                        .filter(q -> q.isFinishedBy(player.getUsername()))
                        .toList().size());

                Map<String, Integer> abilityPayload = new HashMap<>();
                PlayerAbilities playerAbilities = player.getPlayerAbilities();

                abilityPayload.put("farming_ability", playerAbilities.getAbilityLevel(Ability.FARMING));
                abilityPayload.put("mining_ability", playerAbilities.getAbilityLevel(Ability.MINING));
                abilityPayload.put("nature_ability", playerAbilities.getAbilityLevel(Ability.NATURE));
                abilityPayload.put("fishing_ability", playerAbilities.getAbilityLevel(Ability.FISHING));

                payload.put("abilities", abilityPayload);

                leaderboard.add(payload);
            }

            for (Player player : players) {
                ClientConnection client = GameServer.getClientHandler().getClientByUsername(player.getUsername());
                if (client != null) {
                    client.send(new Message(Type.LEADERBOARD, Map.of("leaderboard", leaderboard)));
                } else {
                    System.err.println("No client connection for: " + player.getUsername());
                }
            }

        } catch (Exception e) {
            System.err.println("Error in broadcastLeaderboardUpdates: " + e.getMessage());
            e.printStackTrace(); // Very important in a scheduled task
        }
    }


    public String getId() {
        return id;
    }

    public RadioManager getRadioManager() {
        return radioManager;
    }
}

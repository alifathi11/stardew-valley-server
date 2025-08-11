package org.example.model.game_models;

import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.math.Vector2;
import org.example.controller.NPC.CollisionController;
import org.example.data.*;
import org.example.model.consts.Gender;
import org.example.model.consts.MapSize;
import org.example.model.consts.Type;
import org.example.model.lobby_models.Lobby;
import org.example.model.message_center.Message;
import org.example.network.ClientConnection;
import org.example.network.GameServer;
import org.example.model.game_models.PlayerAbilities.Ability;
import org.example.network.GameSession;
import org.example.radio.RadioManager;

import java.io.File;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Game {

    // Time
    private final long npcDelta = 100;

    // Unique identifier
    private final String id;
    private final GameSession session;

    // From lobby
    private final Map<String, String> playerNames;
    private final Map<String, Gender> playerGenders;

    // User
    private final Set<String> users;
    private final Map<String, Integer> userToMapNumber;
    private final Map<Integer, Vector2> mapNumberToPosition;

    // Player
    private Map<Player, PlayerMap> playerToPlayerMap;
    private List<Chat> chats;
    private List<Player> players;

    // NPC
    private List<NPC> npcs;
    private Map<String, List<Quest>> questByNpc;
    private final Map<String, Quest> quests;


    // Relation
    private final List<NPCRelation> npcRelations;
    private final List<PlayerRelation> playerRelations;

    // Animal
    private Set<Animal> animals;

    // Shop
    private Map<String, Shop> shops;

    // Map
    private GameMap map;
    private TiledMap tiledMap;

    // Radio
    private final RadioManager radioManager;

    // Vote
    private Vote vote;
    private boolean voteActive;

    // Executor
    private final ScheduledExecutorService leaderboardExecutor;
    private final ScheduledExecutorService updateNPC;
    private final ScheduledExecutorService npcBroadcastExecutor;


    public Game(String id, GameSession session, Lobby lobby) {

        //Unique Identifier
        this.id = id;
        this.session = session;

        // From lobby
        this.playerGenders = lobby.getPlayerGenders();
        this.userToMapNumber = lobby.getUserToMapNumber();

        // User
        this.users = lobby.getMembers();
        this.playerNames = lobby.getPlayerNames();
        this.mapNumberToPosition = new HashMap<>();

        // Animal
        this.animals = new HashSet<>();

        // Shop
        this.shops = new HashMap<>();

        // NPC
        this.questByNpc = new HashMap<>();
        this.npcs = new ArrayList<>();
        this.quests = new HashMap<>();

        // Player
        this.players = new ArrayList<>();
        this.playerToPlayerMap = new HashMap<>();
        this.chats = new ArrayList<>();

        // Relation
        this.npcRelations = new ArrayList<>();
        this.playerRelations = new ArrayList<>();

        // Executors
        this.leaderboardExecutor = Executors.newScheduledThreadPool(1);
        this.updateNPC = Executors.newScheduledThreadPool(1);
        this.npcBroadcastExecutor = Executors.newScheduledThreadPool(1);

        leaderboardExecutor.scheduleAtFixedRate(this::broadcastLeaderboardUpdates, 0, 1000, TimeUnit.MILLISECONDS);
        updateNPC.scheduleAtFixedRate(this::updateNPC, 0, npcDelta, TimeUnit.MILLISECONDS);
        npcBroadcastExecutor.scheduleAtFixedRate(this::broadcastNPC, 0, 100, TimeUnit.MILLISECONDS);

        // Radio
        this.radioManager = new RadioManager();

        // Vote
        this.voteActive = false;

        // Collision controller
        CollisionController.getInstance().init(this);

        // Initialize
        initialize();

    }



    private void initialize() {
        // Build map entities
        buildPlayers();
        buildNPCs();
        buildQuests();

        // Build Shops
        buildShops();

        // Build map
        buildMap();

        // Build player maps
        buildPlayerMaps();

        // Build chats
        buildChats();

        // Build Relations
        buildNPCRelations();
        buildPlayerRelations();
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

        this.tiledMap = new TmxMapLoader().load("src/main/assets/Map1.tmx");
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

                for (Quest quest : entry.getValue()) {
                    quests.put(quest.getId(), quest);
                }

            } else {
                System.err.println("NPC not found for name: " + name);
            }
        }

    }

    private void buildNPCRelations() {
        for (Player player : players) {
            for (NPC npc : npcs) {
                NPCRelation relation = new NPCRelation(player, npc);
                npcRelations.add(relation);
                player.addNPCRelation(relation);
            }
        }
    }

    private void buildPlayerRelations() {
        for (int i = 0; i < players.size() - 1; i++) {
            for (int j = i + 1; j < players.size(); j++) {
                Player p1 = players.get(i);
                Player p2 = players.get(j);

                PlayerRelation relation = new PlayerRelation(p1, p2);

                p1.addPlayerRelation(relation);
                p2.addPlayerRelation(relation);

                playerRelations.add(relation);
            }
        }
    }

    private void buildShops() {

        // Build shops
        File shopFile = new File("src/main/java/org/example/data/shops.json");
        shops = ShopLoader.loadShopsFromFile(shopFile);

        // Add shop NPCs
        for (Shop shop : shops.values()) {
            NPC npc = shop.getOwner();
            npcs.add(npc);
        }

        // Load shop items
        if (shops == null) return;

        File itemFile = new File("src/main/java/org/example/data/shop_items.json");

        for (Shop shop : shops.values()) {
            shop.setShopItems(ShopItemLoader.getShopItems(itemFile, shop.getShopName()));
        }
    }

    public List<Player> getPlayers() {
        return players;
    }

    public Player getPlayer(String username) {
        return players.stream().filter(p -> p.getUsername().equalsIgnoreCase(username)).toList().getFirst();
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
            e.printStackTrace();
        }
    }

    private void updateNPC() {
        for (NPC npc : npcs) {
            for (Player player : players) {
                npc.getController().update((float) (npcDelta * 0.01), player);
            }
        }
    }

    private void broadcastNPC() {
        try {
            List<Map<String, Object>> payloads = new ArrayList<>();
            for (NPC npc : npcs) {
                Map<String, Object> payload = new HashMap<>();
                payload.put("npc_id", npc.getId());
                payload.put("pos_x", npc.getPosition().x);
                payload.put("pos_y", npc.getPosition().y);

                payloads.add(payload);
            }

            for (Player player : players) {
                ClientConnection client = GameServer.getClientHandler().getClientByUsername(player.getUsername());
                if (client != null) {
                    client.send(new Message(Type.NPC_MOVE, Map.of("npc", payloads)));
                } else {
                    System.err.println("No client connection for: " + player.getUsername());
                }
            }

        } catch (Exception e) {
            System.err.println("Error in broadcastNPC: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public String getId() {
        return id;
    }

    public RadioManager getRadioManager() {
        return radioManager;
    }

    public boolean startVoteFire(String target) {
        if (!users.contains(target)) {
            return false;
        }

        if (voteActive) {
            return false;
        }

        this.vote = new Vote(Vote.VoteType.FIRE_PLAYER, target);
        this.voteActive = true;
        return true;
    }

    public boolean startVoteForceTerminate() {
        if (voteActive) {
            return false;
        }

        this.vote = new Vote(Vote.VoteType.FORCE_TERMINATE);
        this.voteActive = true;
        return true;
    }

    public void checkFire() {
        if (this.vote.getType() != Vote.VoteType.FIRE_PLAYER) {
            return;
        }

        int positive = this.vote.getPositive().get();

        if (positive > users.size() / 2) {
            fire(vote.getTargetUsername());
        }

    }

    public void checkForceTerminate() {
        if (this.vote.getType() != Vote.VoteType.FORCE_TERMINATE) {
            return;
        }

        int positive = this.vote.getPositive().get();

        if (positive > users.size() / 2) {
            terminate();
        }
    }

    private void fire(String username) {
        users.remove(username);
        userToMapNumber.remove(username);

        Player player = players.stream().filter(p -> p.getUsername().equalsIgnoreCase(username)).toList().getFirst();
        playerToPlayerMap.remove(player);

        players.remove(player);
        chats.removeIf((e) -> e.getFirst() == player || e.getSecond() == player);

        session.broadcast(new Message(Type.FIRED, Map.of(
                "username", username,
                "content", username + " has been deported from the game."
        )));
    }

    private void terminate() {
        session.broadcast(new Message(Type.TERMINATED, Map.of(
                "content", "game has been terminated by vote."
        )));

        this.finish();
    }

    public void finish() {
        for (String user : users) {
            ClientConnection client = GameServer.getClientHandler().getClientByUsername(user);
            client.setGameSession(null);
        }
    }

    public Vote getVote() {
        return vote;
    }

    public TiledMap getTiledMap() {
        return tiledMap;
    }

    public NPC getNPC(String id) {
        return npcs.stream().filter(n -> n.getId().equals(id)).toList().getFirst();
    }

    public NPCRelation getNPCRelation(Player player, NPC npc) {
        for (NPCRelation relation : npcRelations) {
            if ((relation.getFirst() == player) && (relation.getSecond() == npc)) {
                return relation;
            }
        }

        return null;
    }

    public PlayerRelation getPlayerRelation(Player p1, Player p2) {
        for (PlayerRelation relation : playerRelations) {
            if ((p1 == relation.getFirst() && p2 == relation.getSecond())
                || (p1 == relation.getSecond() && p2 == relation.getFirst())) {
                return relation;
            }
        }

        return null;
    }

    public Quest getQuest(String id) {
        return quests.get(id);
    }

    public Map<String, Shop> getShops() {
        return shops;
    }

    public Shop getShop(String id) {
        return shops.get(id);
    }
}

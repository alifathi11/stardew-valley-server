package org.example.model.game_models;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import org.example.model.consts.Gender;
import org.example.model.message_center.Message;
import org.hibernate.dialect.MariaDBDialect;

import java.util.*;

public class Player {
    private String id;
    private String username;
    private String name;
    private Gender gender;
    private List<Quest> quests;
    private PlayerAbilities playerAbilities;
    private Vector2 position;
    private PlayerMap playerMap;

    private Wallet wallet;

    // Relations
    private List<NPCRelation> npcRelations;
    private List<PlayerRelation> playerRelations;
    private Player spouse;
    private int rejectedDays;

    private Map<String, MarriageProposal> proposals;

    private Map<String, Gift> receivedGifts;
    private Map<String, Gift> sentGifts;

    private Map<String, Trade> trades;

    // Graphics
    private Rectangle collisionRect;
    public static final float width = 16f;
    public static final float height = 32f;

    public Player(String id,
                  String username,
                  String name,
                  Gender gender,
                  Vector2 initialPosition) {

        this.id = id;
        this.username = username;
        this.name = name;
        this.gender = gender;
        this.position = initialPosition;
        this.quests = new ArrayList<>();

        this.wallet = new Wallet(0);

        this.playerAbilities = new PlayerAbilities();
        this.npcRelations = new ArrayList<>();
        this.playerRelations = new ArrayList<>();
        this.proposals = new HashMap<>();

        this.trades = new HashMap<>();

        this.receivedGifts = new HashMap<>();
        this.sentGifts = new HashMap<>();

        this.collisionRect = new Rectangle(
                (position.x - width / 2f),
                (position.y - height / 2f),
                width,
                height
        );
    }

    public Player(String id,
                  String username,
                  String name,
                  Gender gender,
                  Wallet wallet,
                  List<Quest> quests,
                  PlayerAbilities playerAbilities,
                  Map<String, List<Message>> chats) {

        this.id = id;
        this.username = username;
        this.name = name;
        this.gender = gender;
        this.quests = quests;
        this.playerAbilities = playerAbilities;
    }

    public List<Quest> getQuests() {
        return quests;
    }

    public void setQuests(List<Quest> quests) {
        this.quests = quests;
    }

    public PlayerAbilities getPlayerAbilities() {
        return playerAbilities;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public Vector2 getPosition() {
        return position;
    }
    public void setPosition(Vector2 position) {
        this.position = position;
    }

    public void setPosition(float posX, float posY) {
        position.x = posX;
        position.y = posY;
    }

    public void setMap(PlayerMap playerMap) {
        this.playerMap = playerMap;
    }

    public String getId() {
        return id;
    }

    public Rectangle getCollisionRect() {
        return collisionRect;
    }

    public void addQuest(Quest quest) {
        this.quests.add(quest);
    }

    public void finishQuest(Quest quest) {
        quests.remove(quest);
    }

    public List<NPCRelation> getNpcRelations() {
        return npcRelations;
    }

    public List<PlayerRelation> getPlayerRelations() {
        return playerRelations;
    }

    public void addNPCRelation(NPCRelation relation) {
        npcRelations.add(relation);
    }

    public void addPlayerRelation(PlayerRelation relation) {
        playerRelations.add(relation);
    }

    public void addSentGift(Gift gift) {
        sentGifts.put(gift.getId(), gift);
    }

    public void addReceivedGift(Gift gift) {
        receivedGifts.put(gift.getId(), gift);
    }

    public Map<String, Gift> getReceivedGifts() {
        return receivedGifts;
    }

    public Map<String, Gift> getSentGifts() {
        return sentGifts;
    }

    public boolean canRate(String giftId) {
        if (!receivedGifts.containsKey(giftId)) {
            return false;
        }

        Gift gift = receivedGifts.get(giftId);

        if (gift.isRated()) return false;

        return true;
    }

    public Gift getGift(String giftId) {
        return receivedGifts.get(giftId);
    }

    public void addProposal(MarriageProposal proposal) {
        proposals.put(proposal.getId(), proposal);
    }

    public MarriageProposal getProposal(String id) {
        return proposals.get(id);
    }

    public Wallet getWallet() {
        return wallet;
    }

    public void setWallet(Wallet wallet) {
        this.wallet = wallet;
    }

    public void setSpouse(Player spouse) {
        this.spouse = spouse;
    }

    public Player getSpouse() {
        return spouse;
    }

    public void setRejectedDays(int rejectedDays) {
        this.rejectedDays = rejectedDays;
    }

    public int getRejectedDays() {
        return rejectedDays;
    }

    public Trade getTrade(String tradeId) {
        return trades.get(tradeId);
    }

    public void addTrade(Trade trade) {
        trades.put(trade.getId(), trade);
    }
}

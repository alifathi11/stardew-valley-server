package org.example.model.game_models;

import com.badlogic.gdx.math.Vector2;
import org.example.model.consts.Gender;
import org.example.model.message_center.Message;

import java.util.*;

public class Player {
    private String id;
    private String username;
    private String name;
    private Gender gender;
    private Wallet wallet;
    private List<Quest> quests;
    private PlayerAbilities playerAbilities;
    private Vector2 position;
    private PlayerMap playerMap;

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
        this.wallet = new Wallet(0);
        this.quests = new ArrayList<>();
        this.playerAbilities = new PlayerAbilities();
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
        this.wallet = wallet;
        this.quests = quests;
        this.playerAbilities = playerAbilities;
    }

    public List<Quest> getQuests() {
        return quests;
    }

    public void setQuests(List<Quest> quests) {
        this.quests = quests;
    }

    public Wallet getWallet() {
        return wallet;
    }

    public void setWallet(Wallet wallet) {
        this.wallet = wallet;
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

    public void setMap(PlayerMap playerMap) {
        this.playerMap = playerMap;
    }

    public String getId() {
        return id;
    }
}

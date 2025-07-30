package org.example.model;

import java.util.ArrayList;
import java.util.List;

public class Player {
    private String username;
    private String name;
    private Gender gender;
    private Wallet wallet;
    private List<Quest> quests;
    private List<PlayerAbilities> playerAbilities;

    public Player(String username,
                  String name,
                  Gender gender) {
        this.username = username;
        this.name = name;
        this.gender = gender;
        this.wallet = new Wallet(0);
        this.quests = new ArrayList<>();
        this.playerAbilities = new ArrayList<>();
    }

    public Player(String username,
                  String name,
                  Gender gender,
                  Wallet wallet,
                  List<Quest> quests,
                  List<PlayerAbilities> playerAbilities) {

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

    public List<PlayerAbilities> getPlayerAbilities() {
        return playerAbilities;
    }

    public void setPlayerAbilities(List<PlayerAbilities> playerAbilities) {
        this.playerAbilities = playerAbilities;
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
}

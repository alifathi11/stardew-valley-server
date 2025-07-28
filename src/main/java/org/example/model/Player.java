package org.example.model;

import java.util.List;

public class Player {
    private User user;
    private String name;
    private Wallet wallet;
    private List<Quest> quests;
    private List<PlayerAbilities> playerAbilities;

    public Player(User user,
                  String name,
                  Wallet wallet,
                  List<Quest> quests,
                  List<PlayerAbilities> playerAbilities) {

        this.user = user;
        this.name = name;
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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

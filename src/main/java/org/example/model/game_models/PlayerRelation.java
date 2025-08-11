package org.example.model.game_models;

import org.example.model.generic.Pair;

import java.util.ArrayList;
import java.util.List;

public class PlayerRelation extends Pair<Player, Player> {

    private int xp;
    private boolean areMarried;
    private ArrayList<String> talkHistory;
    private int friendshipLevel;
    private final List<Gift> gifts;
    private List<Trade> trades;

    private boolean isFlowerGifted;
    private int numberOfHugsInDay;


    public PlayerRelation(Player player1, Player player2) {
        super(player1, player2);
        this.talkHistory = new ArrayList<>();

        this.xp = 0;
        this.areMarried = false;
        this.friendshipLevel = 0;
        this.isFlowerGifted = false;
        this.gifts = new ArrayList<>();
        this.trades = new ArrayList<>();
        this.numberOfHugsInDay = 0;
    }

    public void setXp(int xp) {
        if (!isFlowerGifted) {
            this.xp = Math.max(0, Math.min(xp, 599));
        } else {
            if (!areMarried) {
                this.xp = Math.max(0, Math.min(xp, 999));
            } else {
                this.xp = Math.max(0, Math.min(xp, 1200));
            }
        }

        if (this.xp < 100) {
            friendshipLevel = 0;
        } else if (this.xp < 300) {
            friendshipLevel = 1;
        } else if (this.xp < 600) {
            friendshipLevel = 2;
        } else if (this.xp < 1000) {
            friendshipLevel = 3;
        } else {
            friendshipLevel = 4;
        }
    }

    public int getFriendshipLevel() {
        return friendshipLevel;
    }

    public boolean AreMarried() {
        return areMarried;
    }

    public int getXp() {
        return xp;
    }

    public void addGift(Gift gift) {
        gifts.add(gift);
    }

    public boolean areMarried() {
        return areMarried;
    }

    public void setAreMarried(boolean areMarried) {
        this.areMarried = areMarried;
    }

    public int getNumberOfHugsInDay() {
        return numberOfHugsInDay;
    }

    public void incrementNumberOfHugsInDay() {
        numberOfHugsInDay++;
    }

    public List<Trade> getTrades() {
        return trades;
    }
}

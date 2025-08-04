package org.example.model;

import java.util.HashMap;
import java.util.Map;

public class Quest {
    private final String npcName;
    private final ItemIDs item;
    private final int itemAmount;
    private final ItemIDs award;
    private final int awardAmount;

    private final Map<String, Boolean> isFinished;

    public Quest(String npcName,
                 ItemIDs item,
                 int itemAmount,
                 ItemIDs award,
                 int awardAmount) {

        this.npcName = npcName;
        this.item = item;
        this.itemAmount = itemAmount;
        this.award = award;
        this.awardAmount = awardAmount;

        this.isFinished = new HashMap<>();
    }

    public String getNpcName() {
        return npcName;
    }

    public int getAwardAmount() {
        return awardAmount;
    }

    public int getItemAmount() {
        return itemAmount;
    }

    public ItemIDs getAward() {
        return award;
    }

    public ItemIDs getItem() {
        return item;
    }

    public void finishedBy(String username) {
        this.isFinished.put(username, true);
    }

    public boolean isFinishedBy(String username) {
        return this.isFinished.containsKey(username) && this.isFinished.get(username);
    }
}

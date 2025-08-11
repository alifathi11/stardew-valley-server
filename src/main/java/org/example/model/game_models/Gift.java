package org.example.model.game_models;

import org.example.model.consts.ItemIDs;
import org.example.model.generic.Pair;
import org.hibernate.cache.spi.support.AbstractReadWriteAccess;

import java.util.ArrayList;

public class Gift extends Pair<Player, Player> {

    private final String id;
    private final ItemIDs item;
    private final int amount;
    private int rate;
    private boolean isRated;

    public Gift(String id,
                Player receiver,
                Player donor,
                ItemIDs item,
                int amount) {

        super(receiver, donor);

        this.id = id;

        this.item = item;
        this.rate = -1;
        this.isRated = false;
        this.amount = amount;
    }

    public ItemIDs getItem() {
        return item;
    }

    public int getRate() {
        return rate;
    }

    public boolean setRate(int rate) {
        if (this.isRated) return false;

        this.rate = rate;
        this.isRated = true;
        return true;
    }

    public int getAmount() {
        return amount;
    }

    public boolean isRated() {
        return isRated;
    }

    public String getId() {
        return id;
    }
}

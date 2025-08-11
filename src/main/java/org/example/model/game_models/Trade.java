package org.example.model.game_models;

import org.example.model.consts.ItemIDs;
import org.example.model.generic.Pair;

public class Trade extends Pair<Player, Player> {

    private final String id
            ;
    private TradeType type;
    private TradeMethod method;
    private TradeState state;

    private final ItemIDs item;
    private final int itemAmount;

    private ItemIDs costItem;
    private int costItemAmount;

    private int price;

    public int getItemAmount() {
        return itemAmount;
    }

    public ItemIDs getItem() {
        return item;
    }

    public int getPrice() {
        return price;
    }

    public ItemIDs getCostItem() {
        return costItem;
    }

    public String getId() {
        return id;
    }

    public TradeState getState() {
        return state;
    }

    public enum TradeType {
        REQUEST,
        OFFER,
        ;
    }

    public enum TradeMethod {
        CASH,
        BARTER,
        ;
    }

    public enum TradeState {
        PENDING,
        ACCEPTED,
        REJECTED,
        ;
    }
}

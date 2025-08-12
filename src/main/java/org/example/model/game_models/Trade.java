package org.example.model.game_models;

import org.example.model.consts.ItemIDs;
import org.example.model.generic.Pair;

public class Trade extends Pair<Player, Player> {

    private final String id;
    private final TradeType type;
    private TradeMethod method;
    private TradeState state;

    private final ItemIDs item;
    private final int itemAmount;

    private ItemIDs costItem;
    private int costItemAmount;

    private int price;

    public Trade(Player p1,
                 Player p2,
                 String id,
                 TradeType type,
                 TradeMethod method,
                 ItemIDs item,
                 int itemAmount,
                 int price) {
        super(p1, p2);

        this.id = id;
        this.type = type;
        this.item = item;
        this.itemAmount = itemAmount;

        if (method == TradeMethod.CASH) {
            this.method = method;
            this.price = price;
        }
    }

    public Trade(Player p1,
                 Player p2,
                 String id,
                 TradeType type,
                 TradeMethod method,
                 ItemIDs item,
                 int itemAmount,
                 ItemIDs costItem,
                 int itemCostAmount) {

        super(p1, p2);

        this.id = id;
        this.type = type;
        this.item = item;
        this.itemAmount = itemAmount;

        if (method == TradeMethod.BARTER) {
            this.method = method;
            this.costItem = costItem;
            this.costItemAmount = itemCostAmount;
        }
    }

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

    public int getCostItemAmount() {
        return costItemAmount;
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

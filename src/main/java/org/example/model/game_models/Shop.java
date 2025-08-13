package org.example.model.game_models;

import com.badlogic.gdx.math.Vector2;
import org.example.model.consts.ItemIDs;

import java.util.HashMap;
import java.util.Map;

public class Shop {
    private final String id;
    private String shopName;
    private NPC owner;
    private int startTime;
    private int endTime;
    private Map<ItemIDs, Integer> shopItems;
    private Map<ItemIDs, Integer> stocks;

    public Shop(String id,
                String shopName,
                NPC owner,
                int startTime,
                int endTime) {

        this.id = id;
        this.shopName = shopName;
        this.owner = owner;
        this.startTime = startTime;
        this.endTime = endTime;
        this.shopItems = new HashMap<>();
        this.stocks = new HashMap<>();
    }

    public String getShopName() {
        return shopName;
    }

    public NPC getOwner() {
        return owner;
    }

    public int getStartTime() {
        return startTime;
    }

    public Map<ItemIDs, Integer> getShopItems() {
        return shopItems;
    }

    public int getEndTime() {
        return endTime;
    }

    public String getId() {
        return id;
    }

    public void setShopItems(Map<ItemIDs, Integer> shopItems) {
        this.shopItems = shopItems;

        // Initialize stocks
        this.stocks = shopItems;
    }

    public int getStock(String itemId) {
        ItemIDs id = ItemIDs.valueOf(itemId);

        return shopItems.get(id);
    }

    public boolean buy(String itemId, int amount) {
        ItemIDs id = ItemIDs.valueOf(itemId);

        if (!shopItems.containsKey(id)) return false;

        if (shopItems.get(id) < amount) return false;

        shopItems.put(id, shopItems.get(id) - amount);
        return true;
    }

    public Map<ItemIDs, Integer> getStocks() {
        return stocks;
    }

    public int getStock(ItemIDs item) {
        return stocks.get(item);
    }

    public void resetDailyLimits() {
        for (var entry : shopItems.entrySet()) {
            ItemIDs item = entry.getKey();
            shopItems.put(item, stocks.get(item));
        }
    }
}

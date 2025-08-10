package org.example.data;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.model.consts.ItemIDs;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class ShopItemLoader {


    public static Map<ItemIDs, Integer> getShopItems(File file, String shopName) {

        ObjectMapper mapper = new ObjectMapper();
        JsonNode root;
        try {
            root = mapper.readTree(file);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        Map<ItemIDs, Integer> itemMap = new HashMap<>();

        for (JsonNode shopNode : root) {
            if (shopNode.get("shop_name").asText().equalsIgnoreCase(shopName)) {
                JsonNode itemsArray = shopNode.get("items");
                if (itemsArray != null && itemsArray.isArray()) {
                    for (JsonNode itemNode : itemsArray) {
                        String itemId = itemNode.get("item_id").asText();
                        int dailyLimit = itemNode.get("daily_limit").asInt();
                        itemMap.put(ItemIDs.valueOf(itemId), dailyLimit);
                    }
                }
                break;
            }
        }
        return itemMap;
    }
}

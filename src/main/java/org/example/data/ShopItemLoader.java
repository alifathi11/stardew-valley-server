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

                        JsonNode baseAttributes = itemNode.get("baseAttributes");
                        if (baseAttributes != null) {
                            int dailyLimit = baseAttributes.get("daily_limit").asInt();
                            int price = baseAttributes.get("price").asInt();

                            System.out.println("item id: " + itemId);
                            System.out.println("daily limit: " + dailyLimit);
                            System.out.println("price: " + price);

                            itemMap.put(ItemIDs.valueOf(itemId.toLowerCase()), dailyLimit);
                        }
                    }
                }
                break;
            }
        }
        return itemMap;

    }
}

package org.example.data;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.model.consts.Gender;
import org.example.model.consts.Profession;
import org.example.model.game_models.NPC;
import org.example.model.game_models.Shop;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class ShopLoader {

    public static Map<String, Shop> loadShopsFromFile(File file) {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root;

        try {
            root = mapper.readTree(file);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        Map<String, Shop> shopMap = new HashMap<>();

        for (JsonNode node : root) {
            // Create NPC
            NPC npc = new NPC(
                    UUID.randomUUID().toString(),
                    node.get("npc_name").asText(),
                    Profession.valueOf(node.get("profession").asText()),
                    Gender.valueOf(node.get("npc_gender").asText())
            );

            // Create Shop
            Shop shop = new Shop(
                    UUID.randomUUID().toString(),
                    node.get("shop_name").asText(),
                    npc,
                    node.get("start_time").asInt(),
                    node.get("end_time").asInt()
            );

            shopMap.put(shop.getId(), shop);
        }

        return shopMap;
    }
}

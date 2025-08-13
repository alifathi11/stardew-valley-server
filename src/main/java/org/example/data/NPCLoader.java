package org.example.data;

import com.badlogic.gdx.math.Vector2;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.model.consts.Gender;
import org.example.model.consts.ItemIDs;
import org.example.model.consts.Profession;
import org.example.model.game_models.NPC;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class NPCLoader {

    public static List<NPC> loadNPCsFromFile(File file) {
        List<NPC> npcs = new ArrayList<>();
        ObjectMapper mapper = new ObjectMapper();

        try {
            JsonNode root = mapper.readTree(file);
            for (JsonNode node : root) {
                try {
                    String name = node.get("name").asText();
                    String characteristics = node.get("characteristics").asText();
                    Gender gender = Gender.valueOf(node.get("gender").asText());
                    Profession profession = Profession.valueOf(node.get("profession").asText());
                    int daysToLastQuest = node.get("days_to_last_quest").asInt();

                    // Load favorite items
                    List<ItemIDs> favoriteItems = new ArrayList<>();
                    JsonNode favoritesNode = node.get("favorite_items");
                    if (favoritesNode != null && favoritesNode.isArray()) {
                        for (JsonNode itemNode : favoritesNode) {
                            favoriteItems.add(ItemIDs.valueOf(itemNode.asText()));
                        }
                    }

                    float initialX = node.get("initial_x").asInt();
                    float initialY = node.get("initial_y").asInt();

                    System.err.println("InitialX: " + initialX + " InitialY: " + initialY);

                    npcs.add(new NPC(
                            UUID.randomUUID().toString(),
                            name,
                            characteristics,
                            profession,
                            gender,
                            new Vector2(initialX, initialY),
                            favoriteItems,
                            daysToLastQuest
                    ));
                } catch (IllegalArgumentException | NullPointerException e) {
                    System.err.println("Skipping invalid NPC entry: " + node);
                }
            }
        } catch (Exception e) {
            System.err.println("Failed to load NPCs: " + e.getMessage());
        }

        return npcs;
    }
}

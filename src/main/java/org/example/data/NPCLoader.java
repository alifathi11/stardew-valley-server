package org.example.data;

import com.badlogic.gdx.math.Vector2;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.model.consts.Gender;
import org.example.model.game_models.NPC;
import org.example.model.consts.Profession;

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
                    Gender gender = Gender.valueOf(node.get("gender").asText());
                    Profession profession = Profession.valueOf(node.get("profession").asText());
                    float initialX = node.get("initial_x").asInt();
                    float initialY = node.get("initial_y").asInt();

                    npcs.add(new NPC(UUID.randomUUID().toString(), name, profession, gender, new Vector2(initialX, initialY)));
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

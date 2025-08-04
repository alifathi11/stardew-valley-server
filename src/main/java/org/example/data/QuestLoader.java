package org.example.data;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.model.consts.ItemIDs;
import org.example.model.game_models.Quest;

import java.io.File;
import java.util.*;

public class QuestLoader {

    public static Map<String, List<Quest>> loadQuestsFromFile(File jsonFile) {

        Map<String, List<Quest>> questsByNPC = new HashMap<>();
        ObjectMapper mapper = new ObjectMapper();

        try {
            JsonNode root = mapper.readTree(jsonFile);
            for (Iterator<String> it = root.fieldNames(); it.hasNext(); ) {
                String npcName = it.next();
                JsonNode questArray = root.get(npcName);

                List<Quest> questList = new ArrayList<>();
                for (JsonNode questNode : questArray) {
                    try {
                        ItemIDs itemId = ItemIDs.valueOf(questNode.get("itemId").asText());
                        int itemAmount = questNode.get("itemAmount").isTextual()
                                ? Integer.parseInt(questNode.get("itemAmount").asText())
                                : questNode.get("itemAmount").asInt();

                        String awardText = questNode.get("award").asText();
                        ItemIDs award = awardText.isEmpty() ? null : ItemIDs.valueOf(awardText);
                        int awardAmount = questNode.get("awardAmount").asInt();

                        questList.add(new Quest(npcName, itemId, itemAmount, award, awardAmount));
                    } catch (IllegalArgumentException | NullPointerException e) {
                        System.err.println("Skipping malformed quest for NPC: " + npcName + " -> " + questNode);
                    }
                }

                questsByNPC.put(npcName, questList);
            }
        } catch (Exception e) {
            System.err.println("Failed to parse quest JSON: " + e.getMessage());
        }

        return questsByNPC;
    }
}
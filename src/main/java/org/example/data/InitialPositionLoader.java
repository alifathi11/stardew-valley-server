package org.example.data;

import com.badlogic.gdx.math.Vector2;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class InitialPositionLoader {

    public static Map<Integer, Vector2> loadMapPositions(File file) {
        Map<Integer, Vector2> mapNumberToPosition = new HashMap<>();
        ObjectMapper mapper = new ObjectMapper();

        try {
            JsonNode root = mapper.readTree(file);

            Iterator<String> fieldNames = root.fieldNames();
            while (fieldNames.hasNext()) {
                String key = fieldNames.next(); // e.g., "map1"

                try {
                    int mapNumber = Integer.parseInt(key.replaceAll("[^0-9]", ""));
                    JsonNode node = root.get(key);

                    float x = node.get("initial_x").floatValue();
                    float y = node.get("initial_y").floatValue();

                    mapNumberToPosition.put(mapNumber, new Vector2(x, y));
                } catch (Exception e) {
                    System.err.println("Skipping invalid map entry: " + key);
                }
            }

        } catch (Exception e) {
            System.err.println("Failed to parse map positions: " + e.getMessage());
        }

        return mapNumberToPosition;
    }
}

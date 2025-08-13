package org.example.data;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

public class AvatarLoader {

    private static String[] paths;
    private static final String FILE_PATH = "src/main/java/org/example/data/avatars.txt";

    public static String[] loadVillagerImagePaths(String filePath) {
        // Read all lines from the file
        List<String> lines;
        try {
            lines = Files.readAllLines(Paths.get(filePath));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // Filter out empty lines and trim whitespace
        List<String> filteredPaths = lines.stream()
                .map(String::trim)
                .filter(line -> !line.isEmpty())
                .collect(Collectors.toList());

        // Convert to String array and return
        return filteredPaths.toArray(new String[0]);
    }

    public static String[] getAvatars() {
        if (paths == null) {
            paths = loadVillagerImagePaths(FILE_PATH);
        }

        return paths;
    }


}
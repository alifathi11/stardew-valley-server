package org.example.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class SessionManager {
    private static final String SESSION_FILE = "src/main/java/org/example/data/session";

    public static void saveSession(String username) {
        try {
            Files.write(Paths.get(SESSION_FILE), username.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String loadSession() {
        try {
            if (Files.exists(Paths.get(SESSION_FILE))) {
                return Files.readString(Paths.get(SESSION_FILE)).trim();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void clearSession() {
        try {
            Files.deleteIfExists(Paths.get(SESSION_FILE));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}


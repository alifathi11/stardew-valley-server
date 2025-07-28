package org.example.utils;

import org.example.network.ServerConfig;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class ConfigLoader {

    public static ServerConfig load(String filePath) {
        int port = 0;

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;

            while ((line = reader.readLine()) != null) {
                line = line.trim();

                // Skip comments and blank lines
                if (line.isEmpty() || line.startsWith("#")) continue;

                String[] parts = line.split("=", 2);
                if (parts.length != 2) continue;

                String key = parts[0].trim();
                String value = parts[1].trim();

                if (key.equalsIgnoreCase("port")) {
                    port = Integer.parseInt(value);
                }
            }

        } catch (IOException e) {
            throw new RuntimeException("Could not load config from " + filePath, e);
        }

        if (port == 0)
            throw new RuntimeException("Port not found or invalid in config file.");

        return new ServerConfig(port);
    }
}

package org.example.LLM;

import java.awt.image.ImagingOpException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class LLMClient {
    public static String queryNPC(String npcName,
                                  String characteristics,
                                  String information,
                                  String message) {
        try {
            URL url = new URL("http://localhost:8080/npc-dialog");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            String jsonInput = String.format(
                    "{\"npcName\": \"%s\", \"playerMessage\": \"%s\", \"characteristics\": \"%s\", \"information\": \"%s\"}",
                    npcName,
                    escape(message),
                    escape(characteristics),
                    escape(information)
            );

            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = jsonInput.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            StringBuilder response = new StringBuilder();
            try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(conn.getInputStream(), "utf-8"))) {
                String responseLine;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }
            }

            String result = response.toString();
            return result.replaceAll(".*\"reply\"\\s*:\\s*\"(.*?)\".*", "$1");
        } catch (IOException e) {
            e.printStackTrace();
            return "Sorry, I couldn't respond right now.";
        }
    }

    private static String escape(String text) {
        return text.replace("\"", "\\\"");
    }
}

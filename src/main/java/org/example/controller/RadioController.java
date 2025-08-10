package org.example.controller;

import org.example.model.consts.Type;
import org.example.model.game_models.Game;
import org.example.model.message_center.Message;
import org.example.model.user.User;
import org.example.network.ClientConnection;
import org.example.network.GameServer;
import org.example.radio.AudioChunk;
import org.example.radio.RadioManager;
import org.example.repository.UserRepository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class RadioController {

    public Message startBroadcasting(Message message) {
        String username = (String) message.getFromPayload("username");
        var format = (Map<String, Object>) message.getFromPayload("format");

        ClientConnection client = GameServer.getClientHandler().getClientByUsername(username);
        if (client == null) {
            return Message.error(Type.START_BROADCASTING, "client doesn't exist.");
        }

        Game game = client.getGameSession().getGame();
        if (game == null) {
            return Message.error(Type.START_BROADCASTING, "game doesn't exist.");
        }

        RadioManager radioManager = game.getRadioManager();

        radioManager.createSession(username, format);

        return new Message(Type.START_BROADCASTING, Map.of(
                "status", "success",
                "session_id", username,
                "content", "Broadcasting started"
        ));
    }

    public Message sendAudioChunk(Message message) {
        String username = (String) message.getFromPayload("username");
        String sessionId = (String) message.getFromPayload("session_id");
        Long timestamp = ((Number) message.getFromPayload("timestamp")).longValue();
        String base64Data = (String) message.getFromPayload("data");

        if (username == null || sessionId == null || base64Data == null) {
            return Message.error(Type.SEND_AUDIO_CHUNK, "invalid audio chunk data.");
        }

        byte[] audioBytes = Base64.getDecoder().decode(base64Data);

        AudioChunk chunk = new AudioChunk(sessionId, audioBytes, timestamp);

        ClientConnection client = GameServer.getClientHandler().getClientByUsername(username);
        if (client == null) {
            return Message.error(Type.SEND_AUDIO_CHUNK, "client doesn't exist.");
        }

        Game game = client.getGameSession().getGame();
        if (game == null) {
            return Message.error(Type.SEND_AUDIO_CHUNK, "game doesn't exist.");
        }

        RadioManager radioManager = game.getRadioManager();

        radioManager.createOrUpdateSession(sessionId, chunk);

        return Message.success(Type.SEND_AUDIO_CHUNK, "chunk sent successfully.");
    }


    public Message joinRadio(Message message) {
        String username = (String) message.getFromPayload("username");
        String targetUsername = (String) message.getFromPayload("target_username");

        Optional<User> userOpt = UserRepository.getInstance().findByUsername(username);
        if (userOpt.isEmpty()) {
            return Message.error(Type.JOIN_RADIO, "user doesn't exist.");
        }

        ClientConnection client = GameServer.getClientHandler().getClientByUsername(username);
        if (client == null) {
            return Message.error(Type.JOIN_RADIO, "client doesn't exist.");
        }

        Game game = client.getGameSession().getGame();
        if (game == null) {
            return Message.error(Type.JOIN_RADIO, "game doesn't exist.");
        }

        boolean result = game.getRadioManager().addListener(username, targetUsername, client.getChannel());

        if (!result) {
            return Message.error(Type.JOIN_RADIO, "target user is not playing anything!");
        }

        var format = game.getRadioManager().getSessions().get(targetUsername).getFormat();

        Map<String, Object> payload = new HashMap<>();
        payload.put("status", "success");
        payload.put("format", format);
        payload.put("content", "you have joined the radio successfully.");

        return new Message(Type.JOIN_RADIO, payload);

    }

    public Message leaveRadio(Message message) {
        String username = (String) message.getFromPayload("username");

        Optional<User> userOpt = UserRepository.getInstance().findByUsername(username);
        if (userOpt.isEmpty()) {
            return Message.error(Type.LEAVE_RADIO, "user doesn't exist.");
        }

        ClientConnection client = GameServer.getClientHandler().getClientByUsername(username);
        if (client == null) {
            return Message.error(Type.LEAVE_RADIO, "client doesn't exist.");
        }

        Game game = client.getGameSession().getGame();
        if (game == null) {
            return Message.error(Type.LEAVE_RADIO, "game doesn't exist.");
        }

        boolean result = game.getRadioManager().removeListener(username, client.getChannel());

        if (!result) {
            return Message.error(Type.LEAVE_RADIO, "you are not listening to any radio!");
        }

        return Message.success(Type.LEAVE_RADIO, "leaved the radio successfully");
    }

    public Message radioList(Message message) {
        String username = (String) message.getFromPayload("username");

        ClientConnection client = GameServer.getClientHandler().getClientByUsername(username);
        if (client == null) {
            return Message.error(Type.RADIO_LIST, "client doesn't exist.");
        }

        Game game = client.getGameSession().getGame();
        if (game == null) {
            return Message.error(Type.RADIO_LIST, "game doesn't exist.");
        }

        RadioManager radioManager = game.getRadioManager();
        Collection<String> hosts = radioManager.getSessions().keySet().stream().filter(n -> !n.equalsIgnoreCase(username)).toList();

        Map<String, Object> payload = new HashMap<>();
        payload.put("hosts", new ArrayList<>(hosts));

        return new Message(Type.RADIO_LIST, payload);
    }
}

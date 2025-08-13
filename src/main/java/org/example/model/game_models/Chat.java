package org.example.model.game_models;

import org.example.model.message_center.Message;
import org.example.model.generic.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Chat extends Pair<Player, Player> {

    private final List<Message> messages;

    public Chat(Player first, Player second) {
        super(first, second);

        this.messages = new ArrayList<>();
    }

    public void addMessage(Message message) {
        this.messages.add(message);
    }

    public List<Map<String, Object>> getMessages() {
        List<Map<String, Object>> payloads = new ArrayList<>();

        for (Message message : messages) {
            payloads.add(message.getPayload());
        }

        return payloads;
    }
}

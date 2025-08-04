package org.example.model.game_models;

import org.example.model.message_center.Message;
import org.example.model.generic.Pair;

import java.util.ArrayList;
import java.util.List;

public class Chat extends Pair<Player> {

    private final List<Message> messages;

    public Chat(Player first, Player second) {
        super(first, second);

        this.messages = new ArrayList<>();
    }

    public void addMessage(Message message) {
        this.messages.add(message);
    }

    public List<Message> getMessages() {
        return messages;
    }
}

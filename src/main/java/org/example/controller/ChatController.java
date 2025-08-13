package org.example.controller;

import org.example.model.consts.MapSize;
import org.example.model.game_models.*;
import org.example.model.message_center.Message;
import org.example.model.consts.Type;
import org.example.network.ClientConnection;
import org.example.network.GameServer;
import org.example.network.GameSession;
import org.w3c.dom.stylesheets.LinkStyle;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChatController {

    public Message sendMessage(Message message) {
        String fromUser = (String) message.getFromPayload("from_user");
        String toUser = (String) message.getFromPayload("to_user");
        String content = (String) message.getFromPayload("content");

        ClientConnection client = GameServer.getClientHandler().getClientByUsername(fromUser);
        GameSession session;

        if (client == null) {
            return Message.error(Type.SEND_MESSAGE, "client doesn't exist.");
        }

        session = client.getGameSession();

        if (session == null) {
            return Message.error(Type.SEND_MESSAGE, "session doesn't exist");
        }

        if (content == null) {
            return Message.error(Type.SEND_MESSAGE, "message doesn't have content.");
        }

        Game game = session.getGame();

        Message msg = new Message(Type.MESSAGE, message.getPayload());

        game.sendMessage(fromUser, toUser, msg);

        return Message.success(Type.SEND_MESSAGE, "message has been sent successfully");
    }

    public Message showChat(Message message) {
        String username = (String) message.getFromPayload("username");
        String otherUser = (String) message.getFromPayload("other_user");

        ClientConnection client = GameServer.getClientHandler().getClientByUsername(username);
        GameSession session;

        if (client == null) {
            return Message.error(Type.SHOW_CHAT, "client doesn't exist.");
        }

        session = client.getGameSession();

        if (session == null) {
            return Message.error(Type.SHOW_CHAT, "session doesn't exist");
        }

        Game game = session.getGame();

        return new Message(Type.SHOW_CHAT, Map.of(
                "other_user", otherUser,
                "messages", game.getMessage(username, otherUser)));
    }

    public Message sendPublicMessage(Message message) {
        String username = (String) message.getFromPayload("username");
        String content = (String) message.getFromPayload("content");

        if (username == null || content == null) {
            return Message.error(Type.SEND_PUBLIC_MESSAGE, "message format is not valid.");
        }

        ClientConnection client = GameServer.getClientHandler().getClientByUsername(username);
        if (client == null) {
            return Message.error(Type.SEND_PUBLIC_MESSAGE, "client doesn't exist.");
        }

        GameSession session = client.getGameSession();
        if (session == null) {
            return Message.error(Type.SEND_PUBLIC_MESSAGE, "session doesn't exist.");
        }

        Game game = session.getGame();
        if (game == null) {
            return Message.error(Type.SEND_PUBLIC_MESSAGE, "game doesn't exist.");
        }

        List<String> tags = extractTags(content);
        sendTagMessages(session, tags);

        Message msg = new Message(Type.PUBLIC_MESSAGE, message.getPayload());

        game.sendPublicMessage(username, msg);

        return Message.success(Type.SEND_PUBLIC_MESSAGE, "message has been sent successfully");
    }

    public Message showPublicChat(Message message) {
        String username = (String) message.getFromPayload("username");

        if (username == null) {
            return Message.error(Type.SHOW_PUBLIC_CHAT, "message format is not valid.");
        }

        ClientConnection client = GameServer.getClientHandler().getClientByUsername(username);
        if (client == null) {
            return Message.error(Type.SHOW_PUBLIC_CHAT, "client doesn't exist.");
        }

        GameSession session = client.getGameSession();
        if (session == null) {
            return Message.error(Type.SHOW_PUBLIC_CHAT, "session doesn't exist.");
        }

        Game game = session.getGame();
        if (game == null) {
            return Message.error(Type.SHOW_PUBLIC_CHAT, "game doesn't exist.");
        }

        return new Message(Type.SHOW_PUBLIC_CHAT, Map.of(
                "messages", game.getPublicChat())
        );
    }

    public static List<String> extractTags(String text) {
        List<String> tags = new ArrayList<>();
        Pattern pattern = Pattern.compile("@([A-Za-z0-9_]+)");
        Matcher matcher = pattern.matcher(text);

        while (matcher.find()) {
            tags.add(matcher.group(1));
        }

        return tags;
    }

    private void sendTagMessages(GameSession session, List<String> tags) {
        for (String tag : tags) {
            ClientConnection client = GameServer.getClientHandler().getClientByUsername(tag);
            if (client == null || !session.getPlayers().contains(client)) continue;
            client.send(new Message(Type.TAG, "You have been tagged in public chat."));
        }
    }



}

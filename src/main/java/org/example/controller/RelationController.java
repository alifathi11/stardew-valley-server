package org.example.controller;

import org.example.model.game_models.Game;
import org.example.model.message_center.Message;
import org.example.model.consts.Type;
import org.example.network.ClientConnection;
import org.example.network.GameServer;
import org.example.network.GameSession;

import java.util.Map;

public class RelationController {

    public Message showReaction(Message message) {
        String username = (String) message.getFromPayload("username");
        ClientConnection client = GameServer.getClientHandler().getClientByUsername(username);
        if (client != null) {
            GameSession session = client.getGameSession();
            if (session != null) {
                session.broadcast(message, username);
            }
        }

        return Message.success(Type.VOID, "reaction has been sent successfully.");
    }

    public Message sendMessage(Message message) {
        String fromUser = (String) message.getFromPayload("from_user");
        String toUser = (String) message.getFromPayload("to_user");

        ClientConnection client = GameServer.getClientHandler().getClientByUsername(fromUser);
        GameSession session;

        if (client == null) {
            return Message.error(Type.SEND_MESSAGE, "client doesn't exist.");
        }

        session = client.getGameSession();

        if (session == null) {
            return Message.error(Type.SEND_MESSAGE, "session doesn't exist");
        }

        Game game = session.getGame();

        Message msg = new Message(Type.MESSAGE, message.getPayload());

        game.sendMessage(fromUser, toUser, msg);

        return Message.success(Type.SEND_MESSAGE, "message has been sent successfully");
    }

    public Message showChat(Message message) {
        String user1 = (String) message.getFromPayload("user_1");
        String user2 = (String) message.getFromPayload("user_2");

        ClientConnection client = GameServer.getClientHandler().getClientByUsername(user1);
        GameSession session;

        if (client == null) {
            return Message.error(Type.SEND_MESSAGE, "client doesn't exist.");
        }

        session = client.getGameSession();

        if (session == null) {
            return Message.error(Type.SEND_MESSAGE, "session doesn't exist");
        }

        Game game = session.getGame();

        return new Message(Type.SHOW_CHAT, Map.of("messages", game.getMessage(user1, user2)));
    }
}

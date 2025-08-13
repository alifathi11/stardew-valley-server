package org.example.controller;

import org.example.model.consts.Type;
import org.example.model.message_center.Message;
import org.example.network.ClientConnection;
import org.example.network.GameServer;
import org.example.network.GameSession;

public class MapController {

    public Message changeTile(Message message) {
        String username = (String) message.getFromPayload("username");
        ClientConnection client = GameServer.getClientHandler().getClientByUsername(username);
        if (client == null) {
            return Message.error(Type.CHANGE_TILE, "client doesn't exist.");
        }

        GameSession session = client.getGameSession();
        if (session == null) {
            return Message.error(Type.CHANGE_TILE, "session doesn't exist.");
        }

        session.broadcast(message, username);

        return Message.success(Type.VOID, "tile successfully changed.");
    }
}

package org.example.controller;

import org.example.global.LobbyManager;
import org.example.model.Lobby;
import org.example.model.LobbyInvitation;
import org.example.model.Message;
import org.example.model.Type;

import java.nio.channels.SocketChannel;
import java.util.*;

public class LobbyController {

    public Message createLobby(Message message) {

        String hostUsername = (String) message.getFromPayload("host_username");
        boolean isPrivate = Objects.equals(message.getFromPayload("is_private"), "true");
        String password = "";
        if (isPrivate) {
            password = (String) message.getFromPayload("password");
        }
        boolean isVisible = Objects.equals(message.getFromPayload("is_visible"), "true");


        Lobby lobby = LobbyManager.createLobby(hostUsername, isPrivate, isVisible, password);
        Map<String, Object> payload = new HashMap<>();
        payload.put("status", "success");
        payload.put("lobby_id", lobby.getId());
        payload.put("members", lobby.getMembers());
        Message response = new Message(Type.CREATE_LOBBY, payload);
        return response;
    }

    public Message sendInvitation(Message message) {
        String hostUsername = (String) message.getFromPayload("host_username");
        String targetUsername = (String) message.getFromPayload("target_username");
        String lobbyId = (String) message.getFromPayload("lobby_id");

        LobbyInvitation invitation = new LobbyInvitation(hostUsername, targetUsername, lobbyId);
        LobbyInvitation.addInvitation(invitation);

        return Message.success();
    }
}

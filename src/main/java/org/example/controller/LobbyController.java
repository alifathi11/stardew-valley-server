package org.example.controller;

import org.example.global.LobbyManager;
import org.example.model.*;
import org.example.network.ClientConnection;
import org.example.network.GameServer;
import org.example.repository.UserRepository;

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

        String invitationToken = "";
        // TODO

        LobbyInvitation invitation = new LobbyInvitation(hostUsername, targetUsername, lobbyId, invitationToken);
        LobbyInvitation.addInvitation(invitation);

        return Message.success(Type.SEND_INVITATION, "invitation sent successfully.");
    }

    public Message acceptInvitation(Message message) {
        String username = (String) message.getFromPayload("username");
        String lobbyId = (String) message.getFromPayload("lobby_id");
        String invitationToken = (String) message.getFromPayload("invitation_token");

        Lobby lobby = LobbyManager.getLobby(lobbyId);

        // TODO: check token validation
        boolean success = LobbyManager.joinLobby(username, lobbyId);
        if (success) {

            Map<String, Object> joinMessagePayload = new HashMap<>();
            joinMessagePayload.put("lobby_id", lobbyId);
            joinMessagePayload.put("username", username);


            if (UserRepository.getInstance().findByUsername(username).isEmpty()) {
                return Message.error(Type.ACCEPT_INVITATION, "Unknown error.");
            }

            User user = UserRepository.getInstance().findByUsername(username).get();

            joinMessagePayload.put("name", user.getName());
            joinMessagePayload.put("gender", user.getGender());

            Message joinMessage = new Message(Type.JOIN_LOBBY, joinMessagePayload);

            for (String member : lobby.getMembers()) {
                if (member.equalsIgnoreCase(username)) continue;
                ClientConnection client = GameServer.getClientHandler().getClientByUsername(member);
                client.send(joinMessage);
            }

            Map<String, Object> responsePayload = new HashMap<>();
            responsePayload.put("status", "success");
            responsePayload.put("lobby_id", lobbyId);
            return new Message(Type.ACCEPT_INVITATION, responsePayload);

        } else {
            Map<String, Object> payload = new HashMap<>();
            payload.put("status", "error");
            payload.put("error", "Failed to join lobby.");
            payload.put("lobby_id", lobbyId);

            return new Message(Type.ACCEPT_INVITATION, payload);
        }
    }

    public Message chooseNameGender(Message message) {
        String username = (String) message.getFromPayload("username");
        String lobbyId = (String) message.getFromPayload("lobby_id");
        String name = (String) message.getFromPayload("name");
        Gender gender = Gender.fromString((String) message.getFromPayload("gender"));

        Lobby lobby = LobbyManager.getLobby(lobbyId);
        if (lobby == null) {
            return Message.error(Type.CHOOSE_NAME_GENDER, "Lobby doesn't exist.");
        }

        if (!lobby.getMembers().contains(username)) {
            return Message.error(Type.CHOOSE_NAME_GENDER, "You are not in the lobby.");
        }

        lobby.getPlayerNames().put(username, name);
        lobby.getPlayerGenders().put(username, gender);

        Map<String, Object> payload = new HashMap<>();
        payload.put("status", "success");
        payload.put("username", username);
        payload.put("name", name);
        payload.put("gender", gender);

        return new Message(Type.CHOOSE_NAME_GENDER, payload);
    }


}

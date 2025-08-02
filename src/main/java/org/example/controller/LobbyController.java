package org.example.controller;

import com.badlogic.gdx.maps.MapObject;
import org.example.global.LobbyManager;
import org.example.model.*;
import org.example.network.ClientConnection;
import org.example.network.GameServer;
import org.example.repository.LobbyInviteTokenRepository;
import org.example.repository.UserRepository;

import java.awt.*;
import java.util.*;

public class LobbyController {

    public Message createLobby(Message message) {

        String hostUsername = (String) message.getFromPayload("host_username");
        String lobbyName = (String) message.getFromPayload("lobby_name");
        boolean isPrivate = Objects.equals(message.getFromPayload("is_private"), "true");

        String password = "";
        if (isPrivate) {
            password = (String) message.getFromPayload("password");
        }
        boolean isVisible = Objects.equals(message.getFromPayload("is_visible"), "true");


        Lobby lobby = LobbyManager.createLobby(lobbyName, hostUsername, isPrivate, isVisible, password);
        Map<String, Object> payload = new HashMap<>();
        payload.put("status", "success");

        payload.put("lobby_id", lobby.getId());
        payload.put("members", new ArrayList<>(lobby.getMembers()));

        return new Message(Type.CREATE_LOBBY, payload);
    }

    public Message sendInvitation(Message message) {
        String fromUser = (String) message.getFromPayload("from_user");
        String toUser = (String) message.getFromPayload("to_user");
        String lobbyId = (String) message.getFromPayload("lobby_id");

        Lobby lobby = LobbyManager.getLobby(lobbyId);

        String token = LobbyInviteTokenRepository.getInstance().generateToken(toUser, lobbyId);

        Map<String, Object> payload = new HashMap<>();
        payload.put("from_user", fromUser);
        payload.put("lobby_name", lobby.getName());
        payload.put("lobby_id", lobbyId);
        payload.put("members", new ArrayList<>(lobby.getMembers()));
        payload.put("is_private", lobby.isPrivate());
        payload.put("is_visible", lobby.isVisible());
        payload.put("invitation_token", token);

        Message invitaionMessage = new Message(Type.INVITATION, payload);

        ClientConnection client = GameServer.getClientHandler().getClientByUsername(toUser);
        client.send(invitaionMessage);

        return Message.success(Type.SEND_INVITATION, "invitation sent successfully.");
    }

    public Message acceptInvitation(Message message) {
        String username = (String) message.getFromPayload("username");
        String lobbyId = (String) message.getFromPayload("lobby_id");
        String token = (String) message.getFromPayload("invitation_token");


        Optional<String> lobbyIdOpt = LobbyInviteTokenRepository.getInstance().consumeToken(token, username);
        String tokenLobbyId;
        if (lobbyIdOpt.isPresent()) {
            tokenLobbyId = lobbyIdOpt.get();
        } else {
            return Message.error(Type.ACCEPT_INVITATION, "Invalid token.");
        }

        if (!tokenLobbyId.equals(lobbyId)) {
            return Message.error(Type.ACCEPT_INVITATION, "Invalid token.");
        }

        Lobby lobby = LobbyManager.getLobby(lobbyId);

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
                if (client == null) continue;
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

    public Message requestJoin(Message message) {
        String username = (String) message.getFromPayload("username");
        String lobbyId = (String) message.getFromPayload("lobby_id");
        String passwordHash = (String) message.getFromPayload("password");

        Lobby lobby = LobbyManager.getLobby(lobbyId);
        if (lobby == null) {
            return Message.error(Type.REQUEST_JOIN, "Lobby doesn't exist.");
        }

        if (!passwordHash.equals(lobby.getPasswordHash())) {
            return Message.error(Type.REQUEST_JOIN, "Password in wrong.");
        }

        boolean success = LobbyManager.joinLobby(username, lobbyId);
        if (success) {

            Map<String, Object> joinMessagePayload = new HashMap<>();
            joinMessagePayload.put("lobby_id", lobbyId);
            joinMessagePayload.put("username", username);


            if (UserRepository.getInstance().findByUsername(username).isEmpty()) {
                return Message.error(Type.REQUEST_JOIN, "Unknown error.");
            }

            User user = UserRepository.getInstance().findByUsername(username).get();

            joinMessagePayload.put("name", user.getName());
            joinMessagePayload.put("gender", user.getGender());

            // Report to other lobby members
            Message joinMessage = new Message(Type.JOIN_LOBBY, joinMessagePayload);

            for (String member : lobby.getMembers()) {
                if (member.equalsIgnoreCase(username)) continue;
                ClientConnection client = GameServer.getClientHandler().getClientByUsername(member);
                if (client == null) continue;
                client.send(joinMessage);
            }

            // Return Response
            Map<String, Object> payload = new HashMap<>();
            payload.put("status", "success");
            payload.put("content", "You have successfully joined the lobby.");
            payload.put("lobby_id", lobbyId);
            return new Message(Type.REQUEST_JOIN, payload);

        } else {
            Map<String, Object> payload = new HashMap<>();
            payload.put("status", "error");
            payload.put("error", "Failed to join the lobby.");
            payload.put("lobby_id", lobbyId);

            return new Message(Type.REQUEST_JOIN, payload);
        }


    }


    public Message leaveLobby(Message message) {
        String username = (String) message.getFromPayload("username");
        String lobbyId = (String) message.getFromPayload("lobby_id");

        Lobby lobby = LobbyManager.getLobby(lobbyId);
        if (lobby == null) {
            return Message.error(Type.REQUEST_LEAVE_LOBBY, "Lobby doesn't exist.");
        }

        if (!lobby.getMembers().contains(username)) {
            return Message.error(Type.REQUEST_LEAVE_LOBBY, "You are not in the lobby.");
        }

        boolean success = LobbyManager.leaveLobby(username, lobbyId);
        if (success) {
            Map<String, Object> leavePayload = new HashMap<>();
            leavePayload.put("username", username);
            leavePayload.put("lobby_id", lobbyId);

            Message leaveMessage = new Message(Type.LEAVE_LOBBY, leavePayload);

            for (String member : lobby.getMembers()) {
                if (member.equalsIgnoreCase(username)) continue;
                ClientConnection client = GameServer.getClientHandler().getClientByUsername(member);
                if (client == null) continue;
                client.send(leaveMessage);
            }

            Map<String, Object> responsePayload = new HashMap<>();
            responsePayload.put("status", "success");
            responsePayload.put("content", "you have successfully left the lobby.");
            responsePayload.put("lobby_id", lobbyId);

            return new Message(Type.REQUEST_LEAVE_LOBBY, responsePayload);

        } else {
            Map<String, Object> responsePayload = new HashMap<>();
            responsePayload.put("status", "error");
            responsePayload.put("error", "Cannot leave the lobby.");
            responsePayload.put("lobby_id", lobbyId);

            return new Message(Type.REQUEST_LEAVE_LOBBY, responsePayload);
        }
    }


}

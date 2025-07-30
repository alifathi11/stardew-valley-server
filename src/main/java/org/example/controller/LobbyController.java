package org.example.controller;

import com.badlogic.gdx.maps.MapObject;
import org.example.global.LobbyManager;
import org.example.model.*;
import org.example.network.ClientConnection;
import org.example.network.GameServer;
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


        Lobby lobby = LobbyManager.createLobby(hostUsername, lobbyName, isPrivate, isVisible, password);
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


        Map<String, Object> payload = new HashMap<>();
        payload.put("from_user", fromUser);
        payload.put("lobby_id", lobbyId);
        payload.put("members", new ArrayList<>(lobby.getMembers()));
        payload.put("invitation_token", "");

        Message invitaionMessage = new Message(Type.INVITATION, payload);

        ClientConnection client = GameServer.getClientHandler().getClientByUsername(toUser);
        client.send(invitaionMessage);

        return Message.success(Type.SEND_INVITATION, "invitation sent successfully.");
    }

    public Message acceptInvitation(Message message) {
        String username = (String) message.getFromPayload("username");
        String lobbyId = (String) message.getFromPayload("lobby_id");
        String invitationToken = (String) message.getFromPayload("invitation_token");
        String passwordHash = "";

        Lobby lobby = LobbyManager.getLobby(lobbyId);
        if (lobby.isPrivate())
            passwordHash = (String) message.getFromPayload("password");

        // TODO: check token validation
        boolean success = LobbyManager.joinLobby(username, passwordHash, lobbyId);
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

        Lobby lobby = LobbyManager.getLobby(lobbyId);
        if (lobby == null) {
            return Message.error(Type.REQUEST_JOIN, "Lobby doesn't exist.");
        }

        String hostUsername = lobby.getHostUsername();

        Map<String, Object> joinRequestPayload = new HashMap<>();
        joinRequestPayload.put("username", username);

        Message joinRequest = new Message(Type.JOIN_REQUEST, joinRequestPayload);
        ClientConnection connection = GameServer.getClientHandler().getClientByUsername(hostUsername);
        if (connection == null) {
            return Message.error(Type.REQUEST_JOIN, "Lobby doesn't have host currently!");
        }

        connection.send(joinRequest);

        return Message.success(Type.REQUEST_JOIN, "Request has been sent successfully.");
    }

    public Message acceptJoin(Message message) {
        String username = (String) message.getFromPayload("username");
        String targetUser = (String) message.getFromPayload("target_user");
        String lobbyId = (String) message.getFromPayload("lobby_id");


        Lobby lobby = LobbyManager.getLobby(lobbyId);
        if (lobby == null) {
            return Message.error(Type.ACCEPT_JOIN, "Lobby has a problem.");
        }

        if (!username.equalsIgnoreCase(lobby.getHostUsername())) {
            return Message.error(Type.ACCEPT_JOIN, "You cannot accept a join request.");
        }

        String passwordHash = "";
        if (lobby.isPrivate()) {
            passwordHash = (String) message.getFromPayload("password");
        }


        boolean success = LobbyManager.joinLobby(targetUser, passwordHash, lobbyId);
        if (success) {

            Map<String, Object> joinMessagePayload = new HashMap<>();
            joinMessagePayload.put("lobby_id", lobbyId);
            joinMessagePayload.put("username", targetUser);


            if (UserRepository.getInstance().findByUsername(targetUser).isEmpty()) {
                return Message.error(Type.ACCEPT_JOIN, "Unknown error.");
            }

            User user = UserRepository.getInstance().findByUsername(targetUser).get();

            joinMessagePayload.put("name", user.getName());
            joinMessagePayload.put("gender", user.getGender());

            // Report to other lobby members
            Message joinMessage = new Message(Type.JOIN_LOBBY, joinMessagePayload);

            for (String member : lobby.getMembers()) {
                if (member.equalsIgnoreCase(targetUser)) continue;
                ClientConnection client = GameServer.getClientHandler().getClientByUsername(member);
                if (client == null) continue;
                client.send(joinMessage);
            }

            // Report to the target user
            ClientConnection targetUserClient = GameServer.getClientHandler().getClientByUsername(targetUser);
            targetUserClient.send(new Message(Type.JOIN_ACCEPTED, Map.of("lobby_id", lobbyId)));

            // Return Response
            Map<String, Object> payload = new HashMap<>();
            payload.put("status", "success");
            payload.put("message", targetUser + " has successfully joined the lobby.");
            payload.put("lobby_id", lobbyId);
            return new Message(Type.ACCEPT_JOIN, payload);

        } else {
            Map<String, Object> payload = new HashMap<>();
            payload.put("status", "error");
            payload.put("error", targetUser + "failed to join lobby.");
            payload.put("lobby_id", lobbyId);

            return new Message(Type.ACCEPT_JOIN, payload);
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
            responsePayload.put("message", "you have successfully left the lobby.");
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

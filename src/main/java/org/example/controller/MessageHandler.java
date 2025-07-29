package org.example.controller;

import org.example.model.Message;

public class MessageHandler {

    private static final AuthenticationController authController = new AuthenticationController();
    private static final LobbyController lobbyController = new LobbyController();

    public static Message handle(Message request) {
        switch (request.getType()) {
            case LOGIN:
                return authController.login(request);
            case SIGNUP:
                return authController.signup(request);
            case CREATE_LOBBY:
                return lobbyController.createLobby(request);
            case SEND_INVITATION:
                return lobbyController.sendInvitation(request);

            default:
                return Message.error("Invalid argument");
        }
    }
}

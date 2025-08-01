package org.example.controller;

import org.example.model.Message;
import org.example.model.Type;

public class MessageHandler {

    private static final AuthenticationController authController = new AuthenticationController();
    private static final LobbyController lobbyController = new LobbyController();
    private static final GameController gameController = new GameController();

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
            case ACCEPT_INVITATION:
                return lobbyController.acceptInvitation(request);
            case CREATE_GAME:
                return gameController.createGame(request);
            case CHOOSE_MAP:
                return gameController.chooseMap(request);
            case CHOOSE_NAME_GENDER:
                return lobbyController.chooseNameGender(request);
            case REQUEST_JOIN:
                return lobbyController.requestJoin(request);
            case REQUEST_LEAVE_LOBBY:
                return lobbyController.leaveLobby(request);
            case PLAYER_MOVE:
                return gameController.playerMove(request);

            default:
                return Message.error(Type.ERROR, "Invalid argument");
        }
    }
}

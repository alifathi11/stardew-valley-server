package org.example.controller;

import org.example.model.message_center.Message;
import org.example.model.consts.Type;

public class MessageHandler {

    private static final AuthenticationController authController = new AuthenticationController();
    private static final LobbyController lobbyController = new LobbyController();
    private static final GameController gameController = new GameController();
    private static final RelationController relationController = new RelationController();
    private static final ProfileController profileController = new ProfileController();
    private static final RadioController radioController = new RadioController();

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
            case REACTION:
                return relationController.showReaction(request);
            case SEND_MESSAGE:
                return relationController.sendMessage(request);
            case SHOW_CHAT:
                return relationController.showChat(request);
            case CHANGE_USERNAME:
                return profileController.changeUsername(request);
            case CHANGE_EMAIL:
                return profileController.changeEmail(request);
            case CHANGE_PASSWORD:
                return profileController.changePassword(request);
            case CHANGE_NICKNAME:
                return profileController.changeNickname(request);
            case CHANGE_GENDER:
                return profileController.changeGender(request);
            case JOIN_RADIO:
                return radioController.joinRadio(request);
            case LEAVE_RADIO:
                return radioController.leaveRadio(request);
            case START_BROADCASTING:
                return radioController.startBroadcasting(request);
            case SEND_AUDIO_CHUNK:
                return radioController.sendAudioChunk(request);

            default:
                return Message.error(Type.ERROR, "Invalid argument");
        }
    }
}

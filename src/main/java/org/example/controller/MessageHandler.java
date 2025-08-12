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
    private static final MapController mapController = new MapController();
    private static final VoteController voteController = new VoteController();
    private static final ShopController shopController = new ShopController();
    private static final NPCRelationController npcRelationController = new NPCRelationController();
    private static final PlayerRelationController playerRelationController = new PlayerRelationController();
    private static final TradeController tradeController = new TradeController();


    public static Message handle(Message request) {
        switch (request.getType()) {
            case LOGIN:
                return authController.login(request);
            case AUTO_LOGIN:
                return authController.autoLogin(request);
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
            case RADIO_LIST:
                return radioController.radioList(request);
            case CHANGE_TILE:
                return mapController.changeTile(request);
            case LOBBY_LIST:
                return lobbyController.lobbyList(request);
            case PLAYER_LIST:
                return lobbyController.playerList(request);
            case START_VOTE_FIRE:
                return voteController.startVoteFire(request);
            case START_VOTE_FORCE_TERMINATE:
                return voteController.startVoteForceTerminate(request);
            case VOTE_FIRE:
                return voteController.voteFire(request);
            case VOTE_FORCE_TERMINATE:
                return voteController.voteForceTerminate(request);
            case MEET_NPC:
                return npcRelationController.meetNPC(request);
            case GET_QUEST:
                return npcRelationController.getQuest(request);
            case COMPLETE_QUEST:
                return npcRelationController.completeQuest(request);
            case GIFT_NPC:
                return npcRelationController.giftNPC(request);
            case QUEST_LIST:
                return npcRelationController.questList(request);
            case NPC_RELATION_LIST:
                return npcRelationController.npcRelationList(request);
            case SHOP_ITEM_LIST:
                return shopController.shopItemList(request);
            case BUY_ITEM:
                return shopController.butItem(request);
            case SEND_GIFT:
                return playerRelationController.sendGift(request);
            case RECEIVED_GIFT_LIST:
                return playerRelationController.receivedGiftLList(request);
            case SENT_GIFT_LIST:
                return playerRelationController.sentGiftList(request);
            case RATE_GIFT:
                return playerRelationController.rateGift(request);
            case GIVE_FLOWER:
                return playerRelationController.giveFlower(request);
            case PROPOSE:
                return playerRelationController.propose(request);
            case RESPONSE_PROPOSAL:
                return playerRelationController.responseProposal(request);
            case HUG:
                return playerRelationController.hug(request);
            case FRIEND_LIST:
                return playerRelationController.friendList(request);
            case REQUEST_TRADE:
                return tradeController.requestTrade(request);
            case OFFER_TRADE:
                return tradeController.offerTrade(request);
            case RESPONSE_TRADE:
                return tradeController.responseTrade(request);

            default:
                return Message.error(Type.ERROR, "Invalid argument");
        }
    }
}

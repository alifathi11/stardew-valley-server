package org.example.controller;

import ch.qos.logback.core.net.server.Client;
import org.example.model.consts.Gender;
import org.example.model.consts.ItemIDs;
import org.example.model.consts.Type;
import org.example.model.game_models.*;
import org.example.model.message_center.Message;
import org.example.network.ClientConnection;
import org.example.network.GameServer;
import org.example.network.GameSession;

import java.nio.charset.StandardCharsets;
import java.util.*;

public class PlayerRelationController {

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

    public Message sendGift(Message message) {
        String username = (String) message.getFromPayload("username");
        String targetUsername = (String) message.getFromPayload("target_username");
        String itemId = (String) message.getFromPayload("item_id");
        String amountStr = (String) message.getFromPayload("amount");

        int amount;
        try {
            amount = Integer.parseInt(amountStr);
        } catch (NumberFormatException e) {
            return Message.error(Type.SEND_GIFT, "amount is not valid.");
        }

        if (username == null || targetUsername == null || itemId == null) {
            return Message.error(Type.SEND_GIFT, "message format in not valid.");
        }

        ClientConnection client = GameServer.getClientHandler().getClientByUsername(username);
        if (client == null) {
            return Message.error(Type.SEND_GIFT, "client doesn't exist.");
        }

        GameSession session = client.getGameSession();
        if (session == null) {
            return Message.error(Type.SEND_GIFT, "session doesn't exist.");
        }

        Game game = session.getGame();
        if (game == null) {
            return Message.error(Type.SEND_GIFT, "game doesn't exist.");
        }
        Player p1 = game.getPlayer(username);
        Player p2 = game.getPlayer(targetUsername);

        PlayerRelation relation = game.getPlayerRelation(p1, p2);
        if (relation == null) {
            return Message.error(Type.SEND_GIFT, "relation doesn't exist.");
        }

        if (relation.getFriendshipLevel() == 0) {
            return Message.error(Type.SEND_GIFT, "You must be on friendship level 1 to send gift.");
        }

        Gift gift = new Gift(UUID.randomUUID().toString(), p1, p2, ItemIDs.valueOf(itemId), amount);
        relation.addGift(gift);
        p1.addSentGift(gift);
        p2.addReceivedGift(gift);

        ClientConnection targetConnection = GameServer.getClientHandler().getClientByUsername(targetUsername);

        targetConnection.send(new Message(Type.GIFT, Map.of(
                "from_user", username,
                "item_id", itemId,
                "amount", amount,
                "gift_id", gift.getId()
        )));

        Map<String, Object> payload = new HashMap<>();
        payload.put("status", "success");
        payload.put("content", "You have successfully sent the gift to " + targetUsername);
        payload.put("to_user", targetUsername);
        payload.put("item_id", itemId);
        payload.put("amount", amount);
        payload.put("gift_id", gift.getId());

        return new Message(Type.SEND_GIFT, payload);
    }

    public Message receivedGiftLList(Message message) {
        String username = (String) message.getFromPayload("username");

        if (username == null) {
            return Message.error(Type.RECEIVED_GIFT_LIST, "message format in not valid.");
        }

        ClientConnection client = GameServer.getClientHandler().getClientByUsername(username);
        if (client == null) {
            return Message.error(Type.RECEIVED_GIFT_LIST, "client doesn't exist.");
        }

        GameSession session = client.getGameSession();
        if (session == null) {
            return Message.error(Type.RECEIVED_GIFT_LIST, "session doesn't exist.");
        }

        Game game = session.getGame();
        if (game == null) {
            return Message.error(Type.RECEIVED_GIFT_LIST, "game doesn't exist.");
        }
        Player player = game.getPlayer(username);

        Collection<Gift> gifts = player.getReceivedGifts().values();

        List<Map<String, Object>> payloads = new ArrayList<>();
        for (Gift gift : gifts) {
            Map<String, Object> payload = new HashMap<>();

            payload.put("gift_id", gift.getId());
            payload.put("from_user", gift.getFirst().getUsername());
            payload.put("item_id", gift.getItem().name());
            payload.put("amount", gift.getAmount());
            payload.put("rate", gift.isRated() ? Integer.toString(gift.getRate()) : "not rated yet");

            payloads.add(payload);
        }

        return new Message(Type.RECEIVED_GIFT_LIST, Map.of("received_gifts", payloads));
    }


    public Message sentGiftList(Message message) {
        String username = (String) message.getFromPayload("username");

        if (username == null) {
            return Message.error(Type.SENT_GIFT_LIST, "message format in not valid.");
        }

        ClientConnection client = GameServer.getClientHandler().getClientByUsername(username);
        if (client == null) {
            return Message.error(Type.SENT_GIFT_LIST, "client doesn't exist.");
        }

        GameSession session = client.getGameSession();
        if (session == null) {
            return Message.error(Type.SENT_GIFT_LIST, "session doesn't exist.");
        }

        Game game = session.getGame();
        if (game == null) {
            return Message.error(Type.SENT_GIFT_LIST, "game doesn't exist.");
        }
        Player player = game.getPlayer(username);

        Collection<Gift> gifts = player.getSentGifts().values();

        List<Map<String, Object>> payloads = new ArrayList<>();
        for (Gift gift : gifts) {
            Map<String, Object> payload = new HashMap<>();

            payload.put("gift_id", gift.getId());
            payload.put("to_user", gift.getSecond().getUsername());
            payload.put("item_id", gift.getItem().name());
            payload.put("amount", gift.getAmount());
            payload.put("rate", gift.isRated() ? Integer.toString(gift.getRate()) : "not rated yet");

            payloads.add(payload);
        }

        return new Message(Type.SENT_GIFT_LIST, Map.of("sent_gifts", payloads));
    }

    public Message rateGift(Message message) {
        String username = (String) message.getFromPayload("username");
        String giftId = (String) message.getFromPayload("gift_id");
        String rateStr = (String) message.getFromPayload("rate");

        int rate;
        try {
            rate = Integer.parseInt(rateStr);
        } catch (NumberFormatException e) {
            return Message.error(Type.RATE_GIFT, "rate is not valid.");
        }

        if (username == null) {
            return Message.error(Type.RATE_GIFT, "message format in not valid.");
        }

        ClientConnection client = GameServer.getClientHandler().getClientByUsername(username);
        if (client == null) {
            return Message.error(Type.RATE_GIFT, "client doesn't exist.");
        }

        GameSession session = client.getGameSession();
        if (session == null) {
            return Message.error(Type.RATE_GIFT, "session doesn't exist.");
        }

        Game game = session.getGame();
        if (game == null) {
            return Message.error(Type.RATE_GIFT, "game doesn't exist.");
        }
        Player player = game.getPlayer(username);

        if (!player.canRate(giftId)) {
            return Message.error(Type.RATE_GIFT, "you cannot rate this gift.");
        }

        if (!(rate >= 0 && rate <= 5)) {
            return Message.error(Type.RATE_GIFT, "rate is not valid.");
        }

        Gift gift = player.getGift(giftId);
        gift.setRate(rate);

        ClientConnection receiverClient = GameServer.getClientHandler().getClientByUsername(gift.getSecond().getUsername());

        receiverClient.send(new Message(Type.RATE, Map.of(
                "gift_id", gift.getId(),
                "rate", rate
        )));

        return Message.success(Type.RATE_GIFT, "gift has been successfully rated.");
    }


    public Message giveFlower(Message message) {
        String username = (String) message.getFromPayload("username");
        String targetUsername = (String) message.getFromPayload("target_player");
        String flowerId = (String) message.getFromPayload("flower_id");

        if (username == null || targetUsername == null) {
            return Message.error(Type.GIVE_FLOWER, "message format in not valid.");
        }

        ClientConnection client = GameServer.getClientHandler().getClientByUsername(username);
        if (client == null) {
            return Message.error(Type.GIVE_FLOWER, "client doesn't exist.");
        }

        GameSession session = client.getGameSession();
        if (session == null) {
            return Message.error(Type.GIVE_FLOWER, "session doesn't exist.");
        }

        Game game = session.getGame();
        if (game == null) {
            return Message.error(Type.GIVE_FLOWER, "game doesn't exist.");
        }
        Player p1 = game.getPlayer(username);
        Player p2 = game.getPlayer(targetUsername);

        PlayerRelation relation = game.getPlayerRelation(p1, p2);

        if (relation.getFriendshipLevel() < 2) {
            return Message.error(Type.GIVE_FLOWER, "At least friendship level 2 needed to gift a flower.");
        }


        ClientConnection targetPlayerClient = GameServer.getClientHandler().getClientByUsername(targetUsername);
        targetPlayerClient.send(new Message(Type.FLOWER, Map.of(
                "from_user", username,
                "flower_id", flowerId
        )));

        if (relation.getFriendshipLevel() == 2 && relation.getXp() == 599) {
            relation.setXp(600);
        } else {
            relation.setXp(relation.getXp() + 80);
        }

        return new Message(Type.GIVE_FLOWER, Map.of(
                "status", "success",
                "content", "you have given the flower successfully."
        ));
    }

    public Message propose(Message message) {
        String username = (String) message.getFromPayload("username");
        String targetUsername = (String) message.getFromPayload("target_username");

        if (username == null || targetUsername == null) {
            return Message.error(Type.PROPOSE, "message format in not valid.");
        }

        ClientConnection client = GameServer.getClientHandler().getClientByUsername(username);
        if (client == null) {
            return Message.error(Type.PROPOSE, "client doesn't exist.");
        }

        GameSession session = client.getGameSession();
        if (session == null) {
            return Message.error(Type.PROPOSE, "session doesn't exist.");
        }

        Game game = session.getGame();
        if (game == null) {
            return Message.error(Type.PROPOSE, "game doesn't exist.");
        }
        Player p1 = game.getPlayer(username);
        Player p2 = game.getPlayer(targetUsername);

        PlayerRelation relation = game.getPlayerRelation(p1, p2);


        if (p1.getGender() == Gender.FEMALE) {
            return Message.error(Type.PROPOSE, "Sorry, but you need to be Male to propose!");
        }
        if (p2.getGender() != Gender.FEMALE) {
            return Message.error(Type.PROPOSE, "We do not support LGBT yet.");
        }

        if (relation.areMarried()) {
            return Message.error(Type.PROPOSE, "Your are proposing your wife!");
        }

        if (relation.getFriendshipLevel() < 3) {
            return Message.error(Type.PROPOSE, "You need to be on friendship level 3 to be able to propose.");
        }

        ClientConnection targetClient = GameServer.getClientHandler().getClientByUsername(targetUsername);

        MarriageProposal proposal = new MarriageProposal(UUID.randomUUID().toString(), p1, p2);
        p2.addProposal(proposal);

        targetClient.send(new Message(Type.MARRIAGE_PROPOSAL, Map.of(
                "from_user", username,
                "proposal_id", proposal.getId()
        )));


        return new Message(Type.PROPOSE, Map.of(
                "status", "success",
                "to_user", targetUsername,
                "proposal_id", proposal.getId()
        ));
    }

    public Message responseProposal(Message message) {
        String username = (String) message.getFromPayload("username");
        String proposalId = (String) message.getFromPayload("proposal_id");
        String response = (String) message.getFromPayload("response");

        if (username == null) {
            return Message.error(Type.RESPONSE_PROPOSAL, "message format in not valid.");
        }

        ClientConnection client = GameServer.getClientHandler().getClientByUsername(username);
        if (client == null) {
            return Message.error(Type.RESPONSE_PROPOSAL, "client doesn't exist.");
        }

        GameSession session = client.getGameSession();
        if (session == null) {
            return Message.error(Type.RESPONSE_PROPOSAL, "session doesn't exist.");
        }

        Game game = session.getGame();
        if (game == null) {
            return Message.error(Type.RESPONSE_PROPOSAL, "game doesn't exist.");
        }
        Player player = game.getPlayer(username);

        MarriageProposal proposal = player.getProposal(proposalId);
        if (proposal == null) {
            return Message.error(Type.RESPONSE_PROPOSAL, "proposal doesn't exist.");
        }

        Player otherPlayer = proposal.getFirst();
        PlayerRelation relation = game.getPlayerRelation(player, otherPlayer);

        if (response.equalsIgnoreCase("accept")) {

            proposal.setState(MarriageProposal.ProposalState.ACCEPTED);
            relation.setAreMarried(true);
            Wallet newWallet = new Wallet(
                    player.getWallet().getCoin() + otherPlayer.getWallet().getCoin());
            player.setWallet(newWallet);
            otherPlayer.setWallet(newWallet);

            // TODO: get the ring

            relation.setXp(1000);
            player.setSpouse(otherPlayer);
            otherPlayer.setSpouse(player);

            ClientConnection otherClient = GameServer.getClientHandler().getClientByUsername(otherPlayer.getUsername());
            otherClient.send(new Message(Type.PROPOSAL_ACCEPTED, Map.of(
                    "from_user", username,
                    "content", "congratulations on your marriage!"
            )));

            return Message.success(Type.RESPONSE_PROPOSAL, "congratulations on your marriage!");

        } else if (response.equalsIgnoreCase("reject")) {

            proposal.setState(MarriageProposal.ProposalState.REJECTED);
            relation.setXp(0);
            otherPlayer.setRejectedDays(7);

            ClientConnection otherClient = GameServer.getClientHandler().getClientByUsername(otherPlayer.getUsername());
            otherClient.send(new Message(Type.PROPOSAL_REJECTED, Map.of(
                    "form_user", username,
                    "content", "Maybe next time..."
            )));

            return Message.success(Type.RESPONSE_PROPOSAL, "proposal has successfully rejected.");

        } else {
            return Message.error(Type.RESPONSE_PROPOSAL, "message format is not valid.");
        }
    }

    public Message hug(Message message) {
        String username = (String) message.getFromPayload("username");
        String targetUsername = (String) message.getFromPayload("target_username");

        if (username == null || targetUsername == null) {
            return Message.error(Type.HUG, "message format in not valid.");
        }

        ClientConnection client = GameServer.getClientHandler().getClientByUsername(username);
        if (client == null) {
            return Message.error(Type.HUG, "client doesn't exist.");
        }

        GameSession session = client.getGameSession();
        if (session == null) {
            return Message.error(Type.HUG, "session doesn't exist.");
        }

        Game game = session.getGame();
        if (game == null) {
            return Message.error(Type.HUG, "game doesn't exist.");
        }
        Player p1 = game.getPlayer(username);
        Player p2 = game.getPlayer(targetUsername);

        PlayerRelation relation = game.getPlayerRelation(p1, p2);
        if (relation == null) {
            return Message.error(Type.HUG, "relation doesn't exist.");
        }

        if (relation.getNumberOfHugsInDay() <= 3) {
            relation.setXp(relation.getXp() + 60);
        } else {
            return Message.error(Type.HUG, "you are over-hugging " + targetUsername);
        }

        relation.incrementNumberOfHugsInDay();

        ClientConnection targetClient = GameServer.getClientHandler().getClientByUsername(targetUsername);


        client.send(new Message(Type.HUGGING, Map.of(
                "other_player", targetUsername
        )));

        targetClient.send(new Message(Type.HUGGING, Map.of(
                "other_player", username
        )));

        return Message.success(Type.VOID, "you are hugging " + targetUsername);
    }

    public Message friendList(Message message) {
        String username = (String) message.getFromPayload("username");

        if (username == null) {
            return Message.error(Type.FRIEND_LIST, "username not found.");
        }

        ClientConnection client = GameServer.getClientHandler().getClientByUsername(username);
        if (client == null) {
            return Message.error(Type.FRIEND_LIST, "client doesn't exist.");
        }

        GameSession session = client.getGameSession();
        if (session == null) {
            return Message.error(Type.FRIEND_LIST, "session doesn't exist.");
        }

        Game game = session.getGame();
        if (game == null) {
            return Message.error(Type.FRIEND_LIST, "game doesn't exist.");
        }

        Player player = game.getPlayer(username);
        if (player == null) {
            return Message.error(Type.FRIEND_LIST, "player doesn't exist.");
        }

        List<Player> players = game.getPlayers();

        List<Map<String, Object>> payloads = new ArrayList<>();

        for (Player otherPlayer : players) {
            if (otherPlayer == player) continue;

            Map<String, Object> payload = new HashMap<>();

            PlayerRelation relation = game.getPlayerRelation(player, otherPlayer);

            payload.put("player_username", otherPlayer.getUsername());
            payload.put("xp", relation.getXp());
            payload.put("level", relation.getFriendshipLevel());
            payload.put("are_married", relation.areMarried());

            payloads.add(payload);
        }

        return new Message(Type.FRIEND_LIST, Map.of("friend_list", payloads));
    }
}
package org.example.controller;

import org.example.model.consts.Type;
import org.example.model.game_models.Game;
import org.example.model.game_models.Vote;
import org.example.model.message_center.Message;
import org.example.network.ClientConnection;
import org.example.network.GameServer;
import org.example.network.GameSession;
import org.hibernate.engine.spi.ManagedEntity;

import java.util.Map;

public class VoteController {

    public Message startVoteFire(Message message) {
        String username = (String) message.getFromPayload("username");
        String targetUsername = (String) message.getFromPayload("target_username");

        if (username == null || targetUsername == null) {
            return Message.error(Type.START_VOTE_FIRE, "username and/or target username not found.");
        }

        ClientConnection client = GameServer.getClientHandler().getClientByUsername(username);
        if (client == null) {
            return Message.error(Type.START_VOTE_FIRE, "client doesn't exist.");
        }

        GameSession session = client.getGameSession();
        if (session == null) {
            return Message.error(Type.START_VOTE_FIRE, "session doesn't exist.");
        }

        Game game = session.getGame();
        boolean result = game.startVoteFire(targetUsername);
        if (!result) {
            return Message.error(Type.VOTE_FIRE_STARTED, "failed to start vote.");
        }

        session.broadcast(new Message(Type.VOTE_FIRE, Map.of(
                "target_username", targetUsername,
                "content", "vote to deport " + targetUsername + "."
        )), username);

        return Message.success(Type.START_VOTE_FIRE, "vote started.");
    }

    public Message startVoteForceTerminate(Message message) {
        String username = (String) message.getFromPayload("username");

        if (username == null) {
            return Message.error(Type.START_VOTE_FORCE_TERMINATE, "username not found.");
        }

        ClientConnection client = GameServer.getClientHandler().getClientByUsername(username);
        if (client == null) {
            return Message.error(Type.START_VOTE_FORCE_TERMINATE, "client doesn't exist.");
        }

        GameSession session = client.getGameSession();
        if (session == null) {
            return Message.error(Type.START_VOTE_FORCE_TERMINATE, "session doesn't exist.");
        }

        Game game = session.getGame();
        boolean result = game.startVoteForceTerminate();
        if (!result) {
            return Message.error(Type.START_VOTE_FORCE_TERMINATE, "failed to start vote.");
        }

        session.broadcast(new Message(Type.VOTE_FORCE_TERMINATE_STARTED, Map.of(
                "content", "vote to terminate game."
        )), username);

        return Message.success(Type.START_VOTE_FORCE_TERMINATE, "vote started.");
    }

    public Message voteFire(Message message) {
        String username = (String) message.getFromPayload("username");
        boolean positive = (boolean) message.getFromPayload("positive");

        if (username == null) {
            return Message.error(Type.VOTE_FIRE, "username not found.");
        }

        ClientConnection client = GameServer.getClientHandler().getClientByUsername(username);
        if (client == null) {
            return Message.error(Type.VOTE_FIRE, "client doesn't exist.");
        }

        GameSession session = client.getGameSession();
        if (session == null) {
            return Message.error(Type.VOTE_FIRE, "client doesn't exist.");
        }

        Game game = session.getGame();
        if (game == null) {
            return Message.error(Type.VOTE_FIRE, "game doesn't exist.");
        }

        Vote vote = game.getVote();

        if (vote.getType() != Vote.VoteType.FIRE_PLAYER) {
            return Message.error(Type.VOTE_FIRE, "vote type is not valid.");
        }

        if (positive) {
            vote.incrementPositive();
        } else {
            vote.incrementNegative();
        }

        game.checkFire();
        return Message.success(Type.VOTE_FIRE, "voted successfully.");
    }

    public Message voteForceTerminate(Message message) {
        String username = (String) message.getFromPayload("username");
        boolean positive = (boolean) message.getFromPayload("positive");

        if (username == null) {
            return Message.error(Type.VOTE_FORCE_TERMINATE, "username not found.");
        }

        ClientConnection client = GameServer.getClientHandler().getClientByUsername(username);
        if (client == null) {
            return Message.error(Type.VOTE_FORCE_TERMINATE, "client doesn't exist.");
        }

        GameSession session = client.getGameSession();
        if (session == null) {
            return Message.error(Type.VOTE_FORCE_TERMINATE, "client doesn't exist.");
        }

        Game game = session.getGame();
        if (game == null) {
            return Message.error(Type.VOTE_FORCE_TERMINATE, "game doesn't exist.");
        }

        Vote vote = game.getVote();

        if (vote.getType() != Vote.VoteType.FORCE_TERMINATE) {
            return Message.error(Type.VOTE_FORCE_TERMINATE, "vote type is not valid.");
        }

        if (positive) {
            vote.incrementPositive();
        } else {
            vote.incrementNegative();
        }

        game.checkForceTerminate();

        return Message.success(Type.VOTE_FORCE_TERMINATE, "voted successfully.");
    }
}

package org.example.controller;

import com.badlogic.gdx.math.Vector2;
import org.example.LLM.LLMClient;
import org.example.model.consts.ItemIDs;
import org.example.model.game_models.*;
import org.example.model.message_center.Message;
import org.example.model.consts.Type;
import org.example.network.ClientConnection;
import org.example.network.GameServer;
import org.example.network.GameSession;
import org.springframework.context.support.MessageSourceAccessor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RelationController {

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

    public Message sendMessage(Message message) {
        String fromUser = (String) message.getFromPayload("from_user");
        String toUser = (String) message.getFromPayload("to_user");

        ClientConnection client = GameServer.getClientHandler().getClientByUsername(fromUser);
        GameSession session;

        if (client == null) {
            return Message.error(Type.SEND_MESSAGE, "client doesn't exist.");
        }

        session = client.getGameSession();

        if (session == null) {
            return Message.error(Type.SEND_MESSAGE, "session doesn't exist");
        }

        Game game = session.getGame();

        Message msg = new Message(Type.MESSAGE, message.getPayload());

        game.sendMessage(fromUser, toUser, msg);

        return Message.success(Type.SEND_MESSAGE, "message has been sent successfully");
    }

    public Message showChat(Message message) {
        String user1 = (String) message.getFromPayload("user_1");
        String user2 = (String) message.getFromPayload("user_2");

        ClientConnection client = GameServer.getClientHandler().getClientByUsername(user1);
        GameSession session;

        if (client == null) {
            return Message.error(Type.SEND_MESSAGE, "client doesn't exist.");
        }

        session = client.getGameSession();

        if (session == null) {
            return Message.error(Type.SEND_MESSAGE, "session doesn't exist");
        }

        Game game = session.getGame();

        return new Message(Type.SHOW_CHAT, Map.of("messages", game.getMessage(user1, user2)));
    }

    public Message meetNPC(Message message) {
        String username = (String) message.getFromPayload("username");
        String npcId = (String) message.getFromPayload("npc_id");
        String information = (String) message.getFromPayload("information");

        if (username == null || npcId == null || information == null) {
            return Message.error(Type.MEET_NPC, "message format is not valid.");
        }

        ClientConnection client = GameServer.getClientHandler().getClientByUsername(username);
        if (client == null) {
            return Message.error(Type.MEET_NPC, "client doesn't exist.");
        }

        GameSession session = client.getGameSession();
        if (session == null) {
            return Message.error(Type.MEET_NPC, "session doesn't exist.");
        }

        Game game = session.getGame();
        if (game == null) {
            return Message.error(Type.MEET_NPC, "game doesn't exist.");
        }

        // fetch game elements
        NPC npc = game.getNPC(npcId);
        Player player = game.getPlayer(username);

        if (npc == null || player == null) {
            return Message.error(Type.MEET_NPC, "npc and/or player not found");
        }

        NPCRelation relation = game.getNPCRelation(player, npc);

        // fetch response from LLM
        String response = LLMClient.queryNPC(npc.getName(), npc.getCharacteristics(), information, "Player named " + player.getName() + " is greeting you");

        // change relationship points
        if (relation.isFirstMeetInDay()) {
            relation.setFriendShipPoints(relation.getFriendShipPoints() + 20); // TODO: hard coded for now
        }

        relation.setFirstMeetInDay(false);

        Map<String, Object> payload = new HashMap<>();
        payload.put("npc_id", npcId);
        payload.put("npc_response", response);
        payload.put("content", "Your friendship level is now " + relation.getFriendShipLevel());

        return new Message(Type.MEET_NPC, payload);
    }

    public Message giftNPC(Message message) {

        String username = (String) message.getFromPayload("username");
        String npcId = (String) message.getFromPayload("npc_id");
        String itemId = (String) message.getFromPayload("item_id");
        String information = (String) message.getFromPayload("information");

        if (username == null || npcId == null || itemId == null || information == null) {
            return Message.error(Type.GIFT_NPC, "message format is not valid.");
        }

        ClientConnection client = GameServer.getClientHandler().getClientByUsername(username);
        if (client == null) {
            return Message.error(Type.GIFT_NPC, "client doesn't exist.");
        }

        GameSession session = client.getGameSession();
        if (session == null) {
            return Message.error(Type.GIFT_NPC, "session doesn't exist.");
        }

        Game game = session.getGame();
        if (game == null) {
            return Message.error(Type.GIFT_NPC, "game doesn't exist.");
        }

        // fetch game elements
        NPC npc = game.getNPC(npcId);
        Player player = game.getPlayer(username);
        NPCRelation relation = game.getNPCRelation(player, npc);

        // fetch response
        String response;

        // increase friendship points
        int points = 0;

        if (npc.hasFavorite(ItemIDs.valueOf(itemId))) {
            response = LLMClient.queryNPC(npc.getName(), npc.getCharacteristics(), information, "Player named " + player.getName() + " has gifted you your favorite item: " + itemId);
            points += 200;
        } else {
            response = LLMClient.queryNPC(npc.getName(), npc.getCharacteristics(), information, "Player named " + player.getName() + " has gifted you an item you don't like: " + itemId);
            points += 100;
        }

        if (relation.isFirstGiftInDay()) {
            points += 50;
        }

        relation.setFriendShipPoints(relation.getFriendShipPoints() + points);

        Map<String, Object> payload = new HashMap<>();
        payload.put("status", "success");
        payload.put("npc_id", npcId);
        payload.put("response", response);
        payload.put("content", "you have gifted " + npc.getName() + " successfully.");

        return new Message(Type.GIFT_NPC, payload);
    }

    public Message getQuest(Message message) {

        String username = (String) message.getFromPayload("username");
        String npcId = (String) message.getFromPayload("npc_id");
        String information = (String) message.getFromPayload("information");

        if (username == null || npcId == null) {
            return Message.error(Type.GET_QUEST, "message format is not valid.");
        }

        ClientConnection client = GameServer.getClientHandler().getClientByUsername(username);
        if (client == null) {
            return Message.error(Type.GET_QUEST, "client doesn't exist.");
        }

        GameSession session = client.getGameSession();
        if (session == null) {
            return Message.error(Type.GET_QUEST, "session doesn't exist.");
        }

        Game game = session.getGame();
        if (game == null) {
            return Message.error(Type.GET_QUEST, "game doesn't exist.");
        }

        // Fetch game elements
        NPC npc = game.getNPC(npcId);
        Player player = game.getPlayer(username);
        NPCRelation relation = game.getNPCRelation(player, npc);

        int availableQuestNumber = relation.getAvailableQuestNumber();

        if (availableQuestNumber == 2 && relation.getFriendShipLevel() == 0) {
            return Message.error(Type.GET_QUEST, "You have to be on friendship level 1 to get the quest number 2.");
        }

        if (availableQuestNumber == 3 && relation.getDaysToLastQuest() > 0) {
            return Message.error(Type.GET_QUEST, "Not any available quests. " + relation.getDaysToLastQuest() + " days to next quest.");
        }

        Quest newQuest = npc.getQuest(availableQuestNumber);

        if (newQuest == null) {
            return Message.error(Type.GET_QUEST, "Not any available quest.");
        }

        // Add quest to the player
        player.addQuest(newQuest);

        String response = LLMClient.queryNPC(
                npc.getName(),
                npc.getCharacteristics(),
                information,
                "You are giving the player named " + player.getName() + " a new quest.\n" +
                        "He should give you " + newQuest.getItemAmount() + " of " + newQuest.getItem().name() + "\n" +
                        "And the award is " + newQuest.getAwardAmount() + " of " + newQuest.getAward().name()
                );

        Map<String, Object> payload = new HashMap<>();
        payload.put("status", "success");
        payload.put("response", response);
        payload.put("content", "new quest has been added to your active quests.");
        payload.put("username", username);
        payload.put("quest_id", newQuest.getId());
        payload.put("npc_id", npcId);
        payload.put("item", newQuest.getItem().name());
        payload.put("item_amount", newQuest.getItemAmount());
        payload.put("award", newQuest.getAward().name());
        payload.put("award_amount", newQuest.getAwardAmount());

        return new Message(Type.GET_QUEST, payload);
    }

    public Message completeQuest(Message message) {
        String username = (String) message.getFromPayload("username");
        String npcId = (String) message.getFromPayload("npc_id");
        String questId = (String) message.getFromPayload("quest_id");
        String information = (String) message.getFromPayload("information");

        if (username == null || npcId == null || questId == null) {
            return Message.error(Type.COMPLETE_QUEST, "message format is not valid.");
        }

        ClientConnection client = GameServer.getClientHandler().getClientByUsername(username);
        if (client == null) {
            return Message.error(Type.COMPLETE_QUEST, "client doesn't exist.");
        }

        GameSession session = client.getGameSession();
        if (session == null) {
            return Message.error(Type.COMPLETE_QUEST, "session doesn't exist.");
        }

        Game game = session.getGame();
        if (game == null) {
            return Message.error(Type.COMPLETE_QUEST, "game doesn't exist.");
        }

        // Fetch game elements
        NPC npc = game.getNPC(npcId);
        Player player = game.getPlayer(username);
        NPCRelation relation = game.getNPCRelation(player, npc);
        Quest quest = game.getQuest(questId);

        player.finishQuest(quest);
        if (relation.getAvailableQuestNumber() == 2) {
            relation.setDaysToLastQuest(npc.getDaysToLastQuest());
        }
        relation.setAvailableQuestNumber(relation.getAvailableQuestNumber() + 1);

        int awardAmount = quest.getAwardAmount();

        if (relation.getFriendShipLevel() >= 2) {
            awardAmount *= 2;
        }

        String response = LLMClient.queryNPC(
                npc.getName(),
                npc.getCharacteristics(),
                information,
                "Player named " + player.getName() + " has finished your quest and gave you" +
                        quest.getItemAmount() + " of " + quest.getItem().name() + " .\n" +
                        "Now you should give him/her award: " + quest.getAwardAmount() + " of " + quest.getAward().name()
                );


        Map<String, Object> payload = new HashMap<>();
        payload.put("status", "success");
        payload.put("npc_id", npcId);
        payload.put("response", response);
        payload.put("content", "quest completed successfully.");
        payload.put("award", quest.getAward().name());
        payload.put("award_amount", awardAmount);

        return new Message(Type.COMPLETE_QUEST, payload);
    }

    public Message questList(Message message) {
        String username = (String) message.getFromPayload("username");

        if (username == null) {
            return Message.error(Type.QUEST_LIST, "user not found.");
        }

        ClientConnection client = GameServer.getClientHandler().getClientByUsername(username);
        if (client == null) {
            return Message.error(Type.QUEST_LIST, "client doesn't exist.");
        }

        GameSession session = client.getGameSession();
        if (session == null) {
            return Message.error(Type.QUEST_LIST, "session doesn't exist.");
        }

        Game game = session.getGame();
        if (game == null) {
            return Message.error(Type.QUEST_LIST, "game doesn't exist.");
        }

        Player player = game.getPlayer(username);
        if (player == null) {
            return Message.error(Type.QUEST_LIST, "player doesn't exist.");
        }

        List<Quest> quests = player.getQuests();
        List<Map<String, Object>> payloads = new ArrayList<>();

        for (Quest quest : quests) {
            Map<String, Object> payload = new HashMap<>();

            payload.put("quest_id", quest.getId());
            payload.put("npc_name", quest.getNpcName());
            payload.put("item", quest.getItem().name());
            payload.put("item_amount", quest.getItemAmount());
            payload.put("award", quest.getAward().name());
            payload.put("award_amount", quest.getAwardAmount());

            payloads.add(payload);
        }

        return new Message(Type.QUEST_LIST, Map.of("quest_list", payloads));
    }

    public Message npcRelationList(Message message) {
        String username = (String) message.getFromPayload("username");

        if (username == null) {
            return Message.error(Type.NPC_RELATION_LIST, "user not found.");
        }

        ClientConnection client = GameServer.getClientHandler().getClientByUsername(username);
        if (client == null) {
            return Message.error(Type.NPC_RELATION_LIST, "client doesn't exist.");
        }

        GameSession session = client.getGameSession();
        if (session == null) {
            return Message.error(Type.NPC_RELATION_LIST, "session doesn't exist.");
        }

        Game game = session.getGame();
        if (game == null) {
            return Message.error(Type.NPC_RELATION_LIST, "game doesn't exist.");
        }

        Player player = game.getPlayer(username);
        if (player == null) {
            return Message.error(Type.NPC_RELATION_LIST, "player doesn't exist.");
        }

        List<NPCRelation> relations = player.getNpcRelations();

        List<Map<String, Object>> payloads = new ArrayList<>();

        for (NPCRelation relation : relations) {
            Map<String, Object> payload = new HashMap<>();

            payload.put("npc_id", relation.getSecond().getId());
            payload.put("npc_name", relation.getSecond().getName());
            payload.put("friendship_level", relation.getFriendShipLevel());
            payload.put("friendship_points", relation.getFriendShipPoints());

            payloads.add(payload);
        }


        return new Message(Type.NPC_RELATION_LIST, Map.of("relation_list", payloads));
    }

}

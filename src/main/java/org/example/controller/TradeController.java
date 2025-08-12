package org.example.controller;

import org.example.model.consts.ItemIDs;
import org.example.model.consts.Type;
import org.example.model.game_models.Game;
import org.example.model.game_models.Player;
import org.example.model.game_models.PlayerRelation;
import org.example.model.game_models.Trade;
import org.example.model.message_center.Message;
import org.example.network.ClientConnection;
import org.example.network.GameServer;
import org.example.network.GameSession;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class TradeController {

    public Message requestTrade(Message message) {

        String username = (String) message.getFromPayload("username");
        String targetUsername = (String) message.getFromPayload("target_username");
        ItemIDs item = ItemIDs.valueOf((String) message.getFromPayload("item"));
        String itemAmountStr = (String) message.getFromPayload("item_amount");
        Trade.TradeMethod method = Trade.TradeMethod.valueOf((String) message.getFromPayload("method"));
        String priceStr = (String) message.getFromPayload("price");
        ItemIDs costItem = ItemIDs.valueOf((String) message.getFromPayload("cost_item"));
        String costItemAmountStr = (String) message.getFromPayload("cost_item_amount");

        int price = 0;
        int itemAmount = 0;
        int costItemAmount = 0;

        try {
            itemAmount = Integer.parseInt(itemAmountStr);

            if (method == Trade.TradeMethod.CASH) {
                price = Integer.parseInt(priceStr);
            } else if (method == Trade.TradeMethod.BARTER) {
                costItemAmount = Integer.parseInt(costItemAmountStr);
            } else {
                return Message.error(Type.REQUEST_TRADE, "method is not valid.");
            }
        } catch (NumberFormatException e) {
            return Message.error(Type.REQUEST_TRADE, "message is not valid.");
        }

        if (username == null || targetUsername == null) {
            return Message.error(Type.REQUEST_TRADE, "username or target username is null.");
        }

        ClientConnection client = GameServer.getClientHandler().getClientByUsername(username);
        if (client == null) {
            return Message.error(Type.REQUEST_TRADE, "client doesn't exist.");
        }

        GameSession session = client.getGameSession();
        if (session == null) {
            return Message.error(Type.REQUEST_TRADE, "session doesn't exist.");
        }

        Game game = session.getGame();
        if (game == null) {
            return Message.error(Type.REQUEST_TRADE, "game doesn't exist.");
        }

        Player p1 = game.getPlayer(username);
        Player p2 = game.getPlayer(targetUsername);

        Trade trade;
        Map<String, Object> tradePayload = new HashMap<String, Object>();

        if (method == Trade.TradeMethod.CASH) {

            trade = new Trade(
                    p1,
                    p2,
                    UUID.randomUUID().toString(),
                    Trade.TradeType.REQUEST,
                    method,
                    item,
                    itemAmount,
                    price
            );

            tradePayload = Map.of(
                    "from_user", username,
                    "method", method.name(),
                    "item", item,
                    "item_amount", itemAmount,
                    "price", price
            );

        } else {
            trade = new Trade(
                    p1,
                    p2,
                    UUID.randomUUID().toString(),
                    Trade.TradeType.REQUEST,
                    method,
                    item,
                    itemAmount,
                    costItem,
                    costItemAmount
            );

            tradePayload = Map.of(
                    "from_user", username,
                    "method", method.name(),
                    "item", item,
                    "item_amount", itemAmount,
                    "cost_item", costItem.name(),
                    "cost_item_amount", costItemAmount
            );
        }

        p1.addTrade(trade);
        p2.addTrade(trade);

        ClientConnection targetClient = GameServer.getClientHandler().getClientByUsername(targetUsername);

        targetClient.send(new Message(Type.TRADE_REQUEST, tradePayload));

        return Message.success(Type.REQUEST_TRADE, "request has been successfully sent.");
    }

    public Message offerTrade(Message message) {
        String username = (String) message.getFromPayload("username");
        String targetUsername = (String) message.getFromPayload("target_username");
        ItemIDs item = ItemIDs.valueOf((String) message.getFromPayload("item"));
        String itemAmountStr = (String) message.getFromPayload("item_amount");
        Trade.TradeMethod method = Trade.TradeMethod.valueOf((String) message.getFromPayload("method"));
        String priceStr = (String) message.getFromPayload("price");
        ItemIDs costItem = ItemIDs.valueOf((String) message.getFromPayload("cost_item"));
        String costItemAmountStr = (String) message.getFromPayload("cost_item_amount");

        int price = 0;
        int itemAmount = 0;
        int costItemAmount = 0;

        try {
            itemAmount = Integer.parseInt(itemAmountStr);

            if (method == Trade.TradeMethod.CASH) {
                price = Integer.parseInt(priceStr);
            } else if (method == Trade.TradeMethod.BARTER) {
                costItemAmount = Integer.parseInt(costItemAmountStr);
            } else {
                return Message.error(Type.OFFER_TRADE, "method is not valid.");
            }
        } catch (NumberFormatException e) {
            return Message.error(Type.OFFER_TRADE, "message is not valid.");
        }

        if (username == null || targetUsername == null) {
            return Message.error(Type.OFFER_TRADE, "username or target username is null.");
        }

        ClientConnection client = GameServer.getClientHandler().getClientByUsername(username);
        if (client == null) {
            return Message.error(Type.OFFER_TRADE, "client doesn't exist.");
        }

        GameSession session = client.getGameSession();
        if (session == null) {
            return Message.error(Type.OFFER_TRADE, "session doesn't exist.");
        }

        Game game = session.getGame();
        if (game == null) {
            return Message.error(Type.OFFER_TRADE, "game doesn't exist.");
        }

        Player p1 = game.getPlayer(username);
        Player p2 = game.getPlayer(targetUsername);

        Trade trade;
        Map<String, Object> tradePayload = new HashMap<>();

        if (method == Trade.TradeMethod.CASH) {

            trade = new Trade(
                    p1,
                    p2,
                    UUID.randomUUID().toString(),
                    Trade.TradeType.OFFER,
                    method,
                    item,
                    itemAmount,
                    price
            );

            tradePayload = Map.of(
                    "from_user", username,
                    "method", method.name(),
                    "item", item,
                    "item_amount", itemAmount,
                    "price", price
            );

        } else {
            trade = new Trade(
                    p1,
                    p2,
                    UUID.randomUUID().toString(),
                    Trade.TradeType.OFFER,
                    method,
                    item,
                    itemAmount,
                    costItem,
                    costItemAmount
            );

            tradePayload = Map.of(
                    "from_user", username,
                    "method", method.name(),
                    "item", item,
                    "item_amount", itemAmount,
                    "cost_item", costItem.name(),
                    "cost_item_amount", costItemAmount
            );
        }

        p1.addTrade(trade);
        p2.addTrade(trade);

        ClientConnection targetClient = GameServer.getClientHandler().getClientByUsername(targetUsername);

        targetClient.send(new Message(Type.TRADE_OFFER, tradePayload));

        return Message.success(Type.OFFER_TRADE, "offer has been successfully sent.");

    }


    public Message responseTrade(Message message) {
        String username = (String) message.getFromPayload("username");
        String tradeId = (String) message.getFromPayload("trade_id");
        String response = (String) message.getFromPayload("response");

        if (username == null || tradeId == null) {
            return Message.error(Type.RESPONSE_TRADE, "message format is not valid.");
        }

        ClientConnection client = GameServer.getClientHandler().getClientByUsername(username);
        if (client == null) {
            return Message.error(Type.RESPONSE_TRADE, "client doesn't exist.");
        }

        GameSession session = client.getGameSession();
        if (session == null) {
            return Message.error(Type.RESPONSE_TRADE, "session doesn't exist.");
        }

        Game game = session.getGame();
        if (game == null) {
            return Message.error(Type.RESPONSE_TRADE, "game doesn't exist.");
        }

        Player player = game.getPlayer(username);
        if (player == null) {
            return Message.error(Type.RESPONSE_TRADE, "player doesn't exist.");
        }

        Trade trade = player.getTrade(tradeId);
        if (trade == null) {
            return Message.error(Type.RESPONSE_TRADE, "trade doesn't exist.");
        }

        if (player != trade.getSecond()) {
            return Message.error(Type.RESPONSE_TRADE, "you can't response this request.");
        }

        if (Objects.equals(response, "accept")) {

            Player otherPlayer = trade.getFirst();
            ClientConnection otherClient = GameServer.getClientHandler().getClientByUsername(otherPlayer.getUsername());
            if (otherClient == null) {
                return Message.error(Type.RESPONSE_TRADE, "cannot accept this request.");
            }

            otherClient.send(new Message(Type.TRADE_ACCEPTED, Map.of(
                    "trade_id", tradeId
            )));

            PlayerRelation relation = game.getPlayerRelation(player, otherPlayer);
            // TODO: increase points

            return Message.success(Type.RESPONSE_TRADE, "trade has been successfully done.");

        } else if (Objects.equals(response, "reject")) {

            Player otherPlayer = trade.getFirst();
            ClientConnection otherClient = GameServer.getClientHandler().getClientByUsername(otherPlayer.getUsername());
            if (otherClient == null) {
                return Message.error(Type.RESPONSE_TRADE, "cannot accept this request.");
            }

            otherClient.send(new Message(Type.TRADE_REJECTED, Map.of(
                    "trade_id", tradeId
            )));

            PlayerRelation relation = game.getPlayerRelation(player, otherPlayer);
            // TODO: decrease points

            return Message.success(Type.RESPONSE_TRADE, "trade has been successfully rejected.");

        } else {
            return Message.error(Type.RESPONSE_TRADE, "response is not valid.");
        }
    }

}
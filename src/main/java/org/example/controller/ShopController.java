package org.example.controller;

import org.example.model.consts.Type;
import org.example.model.game_models.Game;
import org.example.model.game_models.Shop;
import org.example.model.message_center.Message;
import org.example.network.ClientConnection;
import org.example.network.GameServer;
import org.example.network.GameSession;
import org.hibernate.usertype.BaseUserTypeSupport;

import java.util.HashMap;
import java.util.Map;

public class ShopController {
    public Message shopItemList(Message message) {
        String username = (String) message.getFromPayload("username");
        String shopId = (String) message.getFromPayload("shop_id");

        if (username == null || shopId == null) {
            return Message.error(Type.SHOP_ITEM_LIST, "message format is not valid.");
        }

        ClientConnection client = GameServer.getClientHandler().getClientByUsername(username);
        if (client == null) {
            return Message.error(Type.SHOP_ITEM_LIST, "client doesn't exist.");
        }

        GameSession session = client.getGameSession();
        if (session == null) {
            return Message.error(Type.SHOP_ITEM_LIST, "session doesn't exist.");
        }

        Game game = session.getGame();
        if (game == null) {
            return Message.error(Type.SHOP_ITEM_LIST, "game doesn't exist.");
        }

        Shop shop = game.getShop(shopId);
        if (shop == null) {
            return Message.error(Type.SHOP_ITEM_LIST, "shop doesn't exist.");
        }

        var items = shop.getShopItems();

        Map<String, Object> payload = new HashMap<>();
        payload.put("shop_id", shopId);
        payload.put("items", items);

        return new Message(Type.SHOP_ITEM_LIST, payload);
    }

    public Message butItem(Message message) {
        String username = (String) message.getFromPayload("username");
        String shopId = (String) message.getFromPayload("shop_id");
        String itemId = (String) message.getFromPayload("item_id");
        int amount = (int) message.getFromPayload("amount");

        if (username == null || shopId == null || itemId == null) {
            return Message.error(Type.BUY_ITEM, "message format is not valid.")
        }

        ClientConnection client = GameServer.getClientHandler().getClientByUsername(username);
        if (client == null) {
            return Message.error(Type.BUY_ITEM, "client doesn't exist.");
        }

        GameSession session = client.getGameSession();
        if (session == null) {
            return Message.error(Type.BUY_ITEM, "session doesn't exist.");
        }

        Game game = session.getGame();
        if (game == null) {
            return Message.error(Type.BUY_ITEM, "game doesn't exist.");
        }

        Shop shop = game.getShop(shopId);
        if (shop == null) {
            return Message.error(Type.BUY_ITEM, "shop doesn't exist.");
        }

        if (shop.getStock(itemId) < amount) {
            return Message.error(Type.BUY_ITEM, "not enough stock.");
        }

        boolean result = shop.buy(itemId, amount);
        if (!result) {
            return Message.error(Type.BUY_ITEM, "cannot buy this item. please try again later.");
        }

        Map<String, Object> payload = new HashMap<>();
        payload.put("shop_id", shopId);
        payload.put("item_id", itemId);
        payload.put("amount", amount);

        return new Message(Type.BUY_ITEM, payload);
    }
}

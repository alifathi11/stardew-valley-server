package org.example.update_game;

import org.example.model.consts.ItemIDs;
import org.example.model.consts.Type;
import org.example.model.game_models.Game;
import org.example.model.game_models.NPC;
import org.example.model.game_models.NPCRelation;
import org.example.model.game_models.Player;
import org.example.model.message_center.Message;
import org.example.network.ClientConnection;
import org.example.network.GameServer;
import org.example.network.GameSession;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class UpdateNPC {
    private Game game;

    public UpdateNPC(Game game) {
        this.game = game;
    }

    public void update() {
        updateDaysToLastQuest();
        updateGifts();
    }

    public void updateDaysToLastQuest() {
        List<NPCRelation> relations = game.getNpcRelations();
        for (NPCRelation relation : relations) {
            relation.setDaysToLastQuest(relation.getDaysToLastQuest() - 1);
        }
    }

    public void updateGifts() {
        Random random = new Random();
        List<NPCRelation> NPCRelations = game.getNpcRelations();

        for (NPCRelation npcRelation : NPCRelations) {
            if (npcRelation.getFriendShipLevel() >= 3) {
                Player player = npcRelation.getFirst();
                NPC npc = npcRelation.getSecond();
                List<ItemIDs> items = npc.getFavoriteItems();
                ItemIDs randomItem = items.get(random.nextInt(items.size()));

                ClientConnection client = GameServer.getClientHandler().getClientByUsername(player.getUsername());
                client.send(new Message(Type.NPC_GIFT, Map.of(
                        "from_npc", npc.getId(),
                        "item_id", randomItem.name()
                )));
            }
        }
    }
}

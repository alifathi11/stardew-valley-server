//package org.example.controller.NPC;
//
//import com.badlogic.gdx.Game;
//import com.badlogic.gdx.math.Vector2;
//import com.badlogic.gdx.scenes.scene2d.InputEvent;
//import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
//import com.untildawn.Enums.ItemConsts.ItemType;
//import com.untildawn.controllers.utils.DialogController;
//import com.untildawn.models.App;
//import com.untildawn.models.Game;
//import com.untildawn.models.Items.Inventory;
//import com.untildawn.models.Items.ItemDefinition;
//import com.untildawn.models.NPCs.*;
//import com.untildawn.models.Players.Player;
//import com.untildawn.views.InGameMenus.NPCMenuView;
//
//public class NPCViewController {
//
//    // view
//    private Game game;
//
//    public NPCViewController() {
//        this.game = App.getCurrentGame();
//    }
//
//    public void handleButtons() {
//        view.getTalkButton().addListener(new ClickListener() {
//            public void clicked(InputEvent event, float x, float y) {
//                meetNPC();
//                view.hide();
//            }
//        });
//
//        view.getQuestButton().addListener(new ClickListener() {
//            public void clicked(InputEvent event, float x, float y) {
//                getQuest();
//                view.hide();
//            }
//        });
//
//        view.getGiftButton().addListener(new ClickListener() {
//            public void clicked(InputEvent event, float x, float y) {
//                giftNPC();
//                view.hide();
//            }
//        });
//    }
//

//
//    public void getQuest() {
//
//        // fetch game elements
//        Game game = App.getCurrentGame();
//        Player player = view.getPlayer();
//        NPC npc = view.getNpc();
//        NPCRelation relation = game.getRelation(player, npc);
//
//        // TODO: check in the getQuest method in Relation class
//        int availableQuestNumber = relation.getAvailableQuestNumber();
//
//        if (availableQuestNumber == 2 && relation.getFriendShipLevel() == 0) {
////            this.view.showMessage("You have to be on friendship level 1 to get the quest number 2.");
////            return;
//        }
//
//        if (availableQuestNumber == 3 && relation.getDaysToLastQuest() > 0) {
////            this.view.showMessage(String.format("Not any available quests. %d days to next quest.",
////                relation.getDaysToLastQuest()));
////            return;
//        }
//
//        Quest newQuest = npc.getQuest(availableQuestNumber);
//
//        if (newQuest == null) {
////            this.view.showMessage("Not any available quest.");
////            return;
//        }
//
//        // add quest to the player
//        player.addQuest(newQuest);
//
////        String message = "New quest added successfully:\n" +
////            String.format("You should give %d %s to %s\n",
////                newQuest.getRequestAmount(), newQuest.getRequest().getDisplayName(), NPCName);
////
////        this.view.showMessage(output);
//    }
//}

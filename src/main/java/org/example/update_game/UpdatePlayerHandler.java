//package org.example.update_game;
//
//import org.example.model.game_models.Game;
//import org.example.model.game_models.Player;
//import org.hibernate.sql.Update;
//import org.springframework.aot.hint.SerializationHints;
//
//import javax.swing.text.PlainDocument;
//
//public class UpdatePlayerHandler implements UpdateHandler {
//
//    private final Game game;
//
//    public UpdatePlayerHandler(Game game) {
//        this.game = game;
//    }
//
//    public void update() {
//
//    }
//
//    private void returnPlayers() {
//        for (Player player : game.getPlayers()) {
//            player.returnToHome();
//        }
//    }
//
//    private void updateEnergy() {
//        for (Player player : game.getPlayers()) {
//            player.resetEnergy();
//        }
//    }
//
//    private void checkShippingBin() {
//        ShippingBin shippingBin = game.getShippingBin();
//        shippingBin.check();
//    }
//}

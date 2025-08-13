package org.example.update_game;

import org.example.model.consts.ItemIDs;
import org.example.model.game_models.Game;
import org.example.model.game_models.Shop;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public class UpdateShops implements UpdateHandler {
    private Game game;

    public UpdateShops(Game game) {
        this.game = game;
    }

    public void update() {
        updateDailyLimits();
    }

    private void updateDailyLimits() {
        Collection<Shop> shops = game.getShops().values();
        for (Shop shop : shops) {
            shop.resetDailyLimits();
        }
    }
}

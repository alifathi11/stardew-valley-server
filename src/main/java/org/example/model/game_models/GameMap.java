package org.example.model.game_models;

import com.badlogic.gdx.math.Vector2;
import org.example.model.consts.ItemIDs;
import org.example.model.consts.MapSize;

public class GameMap {
    private Tile[][] map = new Tile[MapSize.MAP_WIDTH.getSize()][MapSize.MAP_HEIGHT.getSize()];

    public Tile getTile(int x, int y) {
        return map[x][y];
    }

    public void build() {
        for (int x = 0; x < MapSize.MAP_WIDTH.getSize(); x++) {
            for (int y = 0; y < MapSize.MAP_HEIGHT.getSize(); y++) {
                Tile tile = new Tile(new Vector2(x, y));
                tile.setId(ItemIDs.VOID);
                map[x][y] = tile;
            }
        }
    }


}

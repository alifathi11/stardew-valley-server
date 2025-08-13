package org.example.model.game_models;

import com.badlogic.gdx.math.Vector2;
import org.example.model.consts.ItemIDs;

public class Tile {
    private final Vector2 position;
    private ItemIDs itemId;

    public Tile(Vector2 position) {
        this.position = position;
    }

    public Vector2 getPosition() {
        return position;
    }

    public void setId(ItemIDs itemId) {
        this.itemId = itemId;
    }

    public ItemIDs getItemId() {
        return itemId;
    }
}

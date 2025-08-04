package org.example.model;

import com.badlogic.gdx.math.Vector2;

public class Tile {
    private final Vector2 position;
    private ItemIDs itemId; // Temp

    public Tile(Vector2 position) {
        this.position = position;
    }

    public Vector2 getPosition() {
        return position;
    }

    public void setId(ItemIDs itemId) {
        this.itemId = itemId;
    }

    public ItemIDs getId() {
        return itemId;
    }
}

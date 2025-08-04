package org.example.model;

import com.badlogic.gdx.math.Vector2;

import java.util.Map;

public class Animal {
    private final String id;
    private String name;
    private Player owner;
    private Vector2 position;
    private boolean isPet;
    private boolean isFed;
    private int friendship;
    private boolean isOutside;
    private boolean hasProduct;
    private Map<ItemInstance, Integer> products;


    public Animal(String id,
                  String name,
                  Player owner,
                  Vector2 position) {
        this.id = id;
        this.name = name;
        this.owner = owner;
        this.position = position;
    }

    public Vector2 getPosition() {
        return position;
    }

    public void setPosition(Vector2 position) {
        this.position = position;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getFriendship() {
        return friendship;
    }

    public void setFriendship(int friendship) {
        this.friendship = friendship;
    }

    public Map<ItemInstance, Integer> getProducts() {
        return products;
    }

    public Player getOwner() {
        return owner;
    }

    public void setOwner(Player owner) {
        this.owner = owner;
    }

    public boolean HasProduct() {
        return hasProduct;
    }

    public void setFed(boolean fed) {
        isFed = fed;
    }

    public String getId() {
        return id;
    }

}

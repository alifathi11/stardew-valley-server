package org.example.model;

import java.util.Map;

public class ItemInstance {

    private String id;
    private ItemDefinition itemDefinition;
    private Map<ItemAttributes, Object> attributes;
    private boolean isWatered;
    private boolean isDroppedByPlayer;

    public ItemInstance(ItemDefinition itemDefinition) {
        this.itemDefinition = itemDefinition;
    }

}

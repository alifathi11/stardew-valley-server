package org.example.model;

import java.util.Map;

public class ItemDefinition {
    private ItemIDs id;
    private ItemType type;
    private String displayName;
    private Map<ItemAttributes, Object> baseAttributes;

    public ItemDefinition() {}

    public ItemDefinition(ItemIDs id,
                          ItemType type,
                          String displayName,
                          Map<ItemAttributes, Object> baseAttributes) {

        this.id = id;
        this.type = type;
        this.displayName = displayName;
        this.baseAttributes = baseAttributes;
    }

    public ItemIDs getId() {
        return id;
    }

    public void setId(ItemIDs id) {
        this.id = id;
    }

    public ItemType getType() {
        return type;
    }

    public void setType(ItemType type) {
        this.type = type;
    }

    public Map<ItemAttributes, Object> getBaseAttributes() {
        return baseAttributes;
    }

    public void setBaseAttributes(Map<ItemAttributes, Object> baseAttributes) {
        this.baseAttributes = baseAttributes;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }
}

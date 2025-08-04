package org.example.model;

public enum MapSize {
    MAP_WIDTH(100),
    MAP_HEIGHT(100),
    PLAYER_MAP_WIDTH(30),
    PLAYER_MAP_HEIGHT(30),
    ;

    private final int size;

    MapSize(int size) {
        this.size = size;
    }

    public int getSize() {
        return size;
    }
}

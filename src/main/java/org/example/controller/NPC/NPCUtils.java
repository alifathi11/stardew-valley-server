package org.example.controller.NPC;

import com.badlogic.gdx.math.Vector2;

public class NPCUtils {
    public static Direction getRandomDirection() {
        Direction[] directions = Direction.values();
        return directions[(int)(Math.random() * directions.length)];
    }

    public static Direction getRandomDirectionExcept(Direction exclude) {
        Direction[] directions = Direction.values();
        Direction choice;
        do {
            choice = directions[(int)(Math.random() * directions.length)];
        } while (choice == exclude);
        return choice;
    }

    public static Vector2 getDirectionVector(Direction dir) {
        switch (dir) {
            case LEFT: return new Vector2(-1, 0);
            case RIGHT: return new Vector2(1, 0);
            case UP: return new Vector2(0, 1);
            case DOWN: return new Vector2(0, -1);
        }
        return new Vector2(0, 0);
    }

    public static Direction getDirectionToFace(Vector2 from, Vector2 to) {
        Vector2 diff = to.cpy().sub(from);
        if (Math.abs(diff.x) > Math.abs(diff.y)) {
            return diff.x > 0 ? Direction.RIGHT : Direction.LEFT;
        } else {
            return diff.y > 0 ? Direction.UP : Direction.DOWN;
        }
    }

}

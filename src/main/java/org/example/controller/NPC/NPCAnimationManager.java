package org.example.controller.NPC;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;

import java.util.HashMap;
import java.util.Map;

public class NPCAnimationManager {
    private final Map<NPCState, Map<Direction, Animation<Sprite>>> animations = new HashMap<>();
    private Animation<Sprite> currentAnimation;
    private NPCState currentState;
    private Direction currentDirection;
    private float stateTime;

    public NPCAnimationManager() {
        changeState(NPCState.IDLE, Direction.DOWN);
    }


    public void changeState(NPCState state, Direction direction) {
        if (state == currentState && direction == currentDirection) return;

        Map<Direction, Animation<Sprite>> dirMap = animations.get(state);
        if (dirMap != null && dirMap.containsKey(direction)) {
            currentAnimation = dirMap.get(direction);
            currentState = state;
            currentDirection = direction;
            stateTime = 0f;
        }
    }

    public void update(float delta) {
        stateTime += delta;
    }
}

package org.example.controller.NPC;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import org.example.model.game_models.NPC;
import org.example.model.game_models.Player;

import static org.example.controller.NPC.NPCUtils.*;


public class NPCController {
    private final NPC npc;
    private final NPCAnimationManager animationManager;
    private final CollisionController  collisionController;

    private final float proximityDistance = 48f;

    private float behaviorTimer = 0;
    private float behaviorDuration = 2f;
    private boolean isIdle = true;

    private Direction walkingDirection = Direction.RIGHT;

    private final Rectangle movementBounds;

    public NPCController(NPC npc, NPCAnimationManager animationManager) {
        this.npc = npc;
        this.animationManager = animationManager;
        this.collisionController = CollisionController.getInstance();
        this.movementBounds = new Rectangle(950, 990, 1200, 1200); // TODO: hard coded for now
    }

    public void update(float delta, Player player) {
        npc.incrementStateTime(delta);
        animationManager.update(delta);

        Vector2 npcPos = npc.getPosition();
        Vector2 playerPos = player.getPosition();
        float distanceToPlayer = npcPos.dst(playerPos);

        // If player is close: Stop and face them
        if (distanceToPlayer < proximityDistance) {
            isIdle = true;
            Direction faceDir = getDirectionToFace(npcPos, playerPos);
            animationManager.changeState(NPCState.IDLE, faceDir);
            npc.setCanTalkNow(true);
            return;
        }

        npc.setCanTalkNow(false);
        behaviorTimer += delta;

        // Switch state after duration
        if (behaviorTimer >= behaviorDuration) {
            behaviorTimer = 0;
            isIdle = !isIdle;

            if (!isIdle) {
                walkingDirection = getRandomDirection();
                behaviorDuration = 2f + (float) Math.random() * 2f;
            } else {
                behaviorDuration = 1f + (float) Math.random();
            }
        }

        if (isIdle) {
            animationManager.changeState(NPCState.IDLE, walkingDirection);
            return;
        }

        // Walking logic
        Vector2 velocity = getDirectionVector(walkingDirection).scl(npc.getSpeed());
        Vector2 newPos = npcPos.cpy().add(velocity.cpy().scl(delta));

        // Prepare a temporary rectangle for future collision test
        float epsilon = 0.1f;
        Rectangle futureRect = new Rectangle(
            newPos.x + epsilon,
            newPos.y + epsilon,
            npc.getCollisionRect().width - 2 * epsilon,
            npc.getCollisionRect().height - 2 * epsilon
        );

        if (movementBounds != null && !movementBounds.contains(newPos)) {
            // If out of bounds, switch to idle and pick a new direction
            isIdle = true;
            walkingDirection = getRandomDirectionExcept(walkingDirection);
            behaviorTimer = 0;
            return;
        }

        // Don't update npc state if the movement would cause collision
        if (collisionController.canNPCTryMove(futureRect, player.getCollisionRect(), npc)) {
            npc.setPosition(newPos); // This also updates the internal collisionRect
            animationManager.changeState(NPCState.WALKING, walkingDirection);
        } else {
            // Pick a new direction on collision
            walkingDirection = getRandomDirectionExcept(walkingDirection);
            behaviorTimer = 0;
            isIdle = true;
        }

    }

    public boolean isTalkable() {
        return npc.canTalkNow();
    }

    public boolean isNear(Player player) {
        float distance = this.npc.getPosition().dst(player.getPosition());
        float interactionRadius = 64f; // TODO: hard coded for now
        return distance < interactionRadius;
    }

}

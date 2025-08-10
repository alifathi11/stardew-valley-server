package org.example.controller.NPC;

import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.EllipseMapObject;
import com.badlogic.gdx.maps.objects.PolygonMapObject;
import com.badlogic.gdx.maps.objects.PolylineMapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.utils.Array;
import org.example.model.game_models.Game;
import org.example.model.game_models.NPC;

public class CollisionController {

    private static CollisionController instance;

    private final Game game;
    private final TiledMap map;
    private final MapObjects collisionObjects;
    private final Array<MapObject> staticCollidables = new Array<>();

    private boolean debugLogCollisions = false;

    private CollisionController(Game game) {
        this.game = game;
        this.map = game.getTiledMap();
        MapLayer collisionLayer = map.getLayers().get("Collisions");

        if (collisionLayer == null)
            throw new IllegalStateException("TiledMap does not have a layer named 'Collisions'.");

        this.collisionObjects = collisionLayer.getObjects();

        // Cache only collidable shapes
        for (MapObject obj : collisionObjects) {
            if (getCollisionShapeType(obj) != null) {
                staticCollidables.add(obj);
            }
        }
    }

    public void init(Game game) {
        if (instance == null) {
            instance = new CollisionController(game);
        }
    }

    public static CollisionController getInstance() {
        if (instance == null) {
            throw new IllegalStateException("CollisionController not initialized. Call init() first.");
        }
        return instance;
    }

    public void enableDebugLog(boolean enabled) {
        this.debugLogCollisions = enabled;
    }

    /**
     * Returns whether the entity can move to the specified rectangle position (no collision).
     */
    public boolean canMoveTo(Rectangle futureRect) {
        return !isBlockedByMap(futureRect) &&
            !isBlockedByNPC(futureRect);
    }

    public boolean canNPCTryMove(Rectangle futureRect, Rectangle playerRect, NPC npc) {
        return !isBlockedByMap(futureRect) &&
            !futureRect.overlaps(playerRect) &&
            !isBlockedByNPC(futureRect, npc);
    }

    private boolean isBlockedByMap(Rectangle entityRect) {
        for (MapObject object : staticCollidables) {
            if (isColliding(entityRect, object)) {
                if (debugLogCollisions) {
                    String name = object.getName();
                    System.out.println("[Collision] Blocked by: " + (name != null ? name : "Unnamed object"));
                }
                return true;
            }
        }
        return false;
    }

    private boolean isBlockedByNPC(Rectangle entityRect, NPC currentNPC) {
        for (NPC npc : game.getNPCs()) {
            if (npc == currentNPC) continue;
            if (npc.getCollisionRect().overlaps(entityRect)) {
                return true;
            }
        }
        return false;
    }

    private boolean isBlockedByNPC(Rectangle entityRect) {
        for (NPC npc : game.getNPCs()) {
            if (npc.getCollisionRect().overlaps(entityRect)) {
                return true;
            }
        }
        return false;
    }




    /**
     * Returns true if the given entity rectangle collides with the given map object.
     */
    private boolean isColliding(Rectangle entityRect, MapObject object) {
        CollisionShape shapeType = getCollisionShapeType(object);

        if (shapeType == null) return false;

        return switch (shapeType) {
            case RECTANGLE -> entityRect.overlaps(((RectangleMapObject) object).getRectangle());
            case ELLIPSE   -> ellipseCollision(entityRect, ((EllipseMapObject) object).getEllipse());
            case POLYGON   -> polygonCollision(entityRect, ((PolygonMapObject) object).getPolygon());
            case POLYLINE  -> polylineCollision(entityRect, ((PolylineMapObject) object).getPolyline());
        };
    }

    private CollisionShape getCollisionShapeType(MapObject object) {
        if (object instanceof RectangleMapObject) return CollisionShape.RECTANGLE;
        if (object instanceof EllipseMapObject) return CollisionShape.ELLIPSE;
        if (object instanceof PolygonMapObject) return CollisionShape.POLYGON;
        if (object instanceof PolylineMapObject) return CollisionShape.POLYLINE;
        return null;
    }

    private boolean ellipseCollision(Rectangle rect, Ellipse ellipse) {
        float cx = ellipse.x + ellipse.width / 2f;
        float cy = ellipse.y + ellipse.height / 2f;
        float rx = ellipse.width / 2f;
        float ry = ellipse.height / 2f;

        float closestX = MathUtils.clamp(cx, rect.x, rect.x + rect.width);
        float closestY = MathUtils.clamp(cy, rect.y, rect.y + rect.height);

        float dx = (closestX - cx) / rx;
        float dy = (closestY - cy) / ry;

        return dx * dx + dy * dy <= 1.0f;
    }

    private boolean polygonCollision(Rectangle rect, Polygon polygon) {
        Polygon rectPoly = rectToPolygon(rect);
        return Intersector.overlapConvexPolygons(rectPoly, polygon);
    }

    private boolean polylineCollision(Rectangle rect, Polyline polyline) {
        float[] vertices = polyline.getTransformedVertices();
        for (int i = 0; i < vertices.length - 2; i += 2) {
            Vector2 p1 = new Vector2(vertices[i], vertices[i + 1]);
            Vector2 p2 = new Vector2(vertices[i + 2], vertices[i + 3]);
            if (Intersector.intersectSegmentRectangle(p1, p2, rect)) {
                return true;
            }
        }
        return false;
    }

    private Polygon rectToPolygon(Rectangle rect) {
        float[] vertices = new float[] {
            rect.x, rect.y,
            rect.x + rect.width, rect.y,
            rect.x + rect.width, rect.y + rect.height,
            rect.x, rect.y + rect.height
        };
        return new Polygon(vertices);
    }
}

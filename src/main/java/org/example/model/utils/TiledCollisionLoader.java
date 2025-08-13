package org.example.model.utils;

import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.EllipseMapObject;
import com.badlogic.gdx.maps.objects.PolygonMapObject;
import com.badlogic.gdx.maps.objects.PolylineMapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public final class TiledCollisionLoader {
    private TiledCollisionLoader() {}

    /**
     * Build a minimal TiledMap that only contains a "Collisions" layer
     * with MapObjects created from a Tiled JSON export.
     *
     * @param jsonPath path to Tiled JSON (e.g., "assets/Map1.json" or "/maps/Map1.json" for classpath)
     * @param fromClasspath if true, load with getResourceAsStream, else from disk path
     */
    public static TiledMap buildCollisionOnlyTiledMap(String jsonPath, boolean fromClasspath) {
        try {
            InputStream in = fromClasspath
                    ? TiledCollisionLoader.class.getResourceAsStream(jsonPath)
                    : new FileInputStream(Paths.get(jsonPath).toFile());
            if (in == null) throw new IllegalArgumentException("Map JSON not found: " + jsonPath);

            ObjectMapper om = new ObjectMapper();
            JsonNode root = om.readTree(in);

            // Find the object layer named "Collisions"
            JsonNode layers = root.get("layers");
            if (layers == null || !layers.isArray())
                throw new IllegalStateException("Invalid Tiled JSON: no 'layers' array.");

            List<MapObject> collidableObjects = new ArrayList<>();

            for (JsonNode layer : layers) {
                String type = layer.path("type").asText("");
                String name = layer.path("name").asText("");
                if (!"objectgroup".equals(type)) continue;
                if (!"collisions".equalsIgnoreCase(name)) continue;

                JsonNode objects = layer.get("objects");
                if (objects == null || !objects.isArray()) continue;

                for (JsonNode obj : objects) {
                    float ox = (float) obj.path("x").asDouble(0);
                    float oy = (float) obj.path("y").asDouble(0);
                    float ow = (float) obj.path("width").asDouble(0);
                    float oh = (float) obj.path("height").asDouble(0);

                    boolean isEllipse = obj.path("ellipse").asBoolean(false);
                    JsonNode polygon = obj.get("polygon");
                    JsonNode polyline = obj.get("polyline");

                    if (polygon != null && polygon.isArray()) {
                        float[] verts = toVertexArray(polygon, ox, oy);
                        collidableObjects.add(new PolygonMapObject(verts));
                    } else if (polyline != null && polyline.isArray()) {
                        float[] verts = toVertexArray(polyline, ox, oy);
                        collidableObjects.add(new PolylineMapObject(verts));
                    } else if (isEllipse) {
                        collidableObjects.add(new EllipseMapObject(ox, oy, ow, oh));
                    } else if (ow > 0 && oh > 0) {
                        collidableObjects.add(new RectangleMapObject(ox, oy, ow, oh));
                    }
                    // (Other Tiled object types are ignored)
                }
                // We found and processed the 'Collisions' layer; no need to scan further
                break;
            }

            // Build a minimal TiledMap with just that layer
            TiledMap out = new TiledMap();
            MapLayer collisions = new MapLayer();
            collisions.setName("Collisions");
            for (MapObject o : collidableObjects) collisions.getObjects().add(o);
            out.getLayers().add(collisions);
            return out;
        } catch (Exception e) {
            throw new RuntimeException("Failed to build collision map from JSON: " + jsonPath, e);
        }
    }

    // Convert Tiled "points" list into a float[] {x1,y1,x2,y2,...} with object (ox,oy) offset added
    private static float[] toVertexArray(JsonNode points, float ox, float oy) {
        float[] verts = new float[points.size() * 2];
        int idx = 0;
        for (JsonNode p : points) {
            verts[idx++] = (float) p.path("x").asDouble() + ox;
            verts[idx++] = (float) p.path("y").asDouble() + oy;
        }
        return verts;
    }
}

package org.example.model.utils;

import org.w3c.dom.*;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.nio.file.Paths;
import java.util.*;
import com.badlogic.gdx.math.Vector2;

public class TmxReader {

    public static class TmxData {
        public final int width, height, tileWidth, tileHeight;
        public final List<Vector2> spawnPoints = new ArrayList<>();
        public final List<List<Vector2>> colliders = new ArrayList<>(); // polygons/polylines/rects as point lists
        public TmxData(int w, int h, int tw, int th) { width=w; height=h; tileWidth=tw; tileHeight=th; }
    }

    public static TmxData read(String tmxRelativePath) {
        try {
            File f = Paths.get(tmxRelativePath).toFile();
            Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(f);
            doc.getDocumentElement().normalize();

            Element map = (Element) doc.getElementsByTagName("map").item(0);
            int width = Integer.parseInt(map.getAttribute("width"));
            int height = Integer.parseInt(map.getAttribute("height"));
            int tileWidth = Integer.parseInt(map.getAttribute("tilewidth"));
            int tileHeight = Integer.parseInt(map.getAttribute("tileheight"));
            TmxData out = new TmxData(width, height, tileWidth, tileHeight);

            NodeList groups = doc.getElementsByTagName("objectgroup");
            for (int i=0; i<groups.getLength(); i++) {
                Element og = (Element) groups.item(i);
                String groupName = og.getAttribute("name").toLowerCase(Locale.ROOT);

                NodeList objects = og.getElementsByTagName("object");
                for (int j=0; j<objects.getLength(); j++) {
                    Element obj = (Element) objects.item(j);

                    float ox = parseFloat(obj.getAttribute("x"));
                    float oy = parseFloat(obj.getAttribute("y"));
                    float ow = parseFloat(obj.getAttribute("width"));
                    float oh = parseFloat(obj.getAttribute("height"));

                    // polygon
                    Node poly = obj.getElementsByTagName("polygon").getLength() > 0
                            ? obj.getElementsByTagName("polygon").item(0) : null;
                    // polyline
                    Node line = obj.getElementsByTagName("polyline").getLength() > 0
                            ? obj.getElementsByTagName("polyline").item(0) : null;

                    if (poly != null || line != null) {
                        Element el = (Element) (poly != null ? poly : line);
                        List<Vector2> pts = parsePoints(el.getAttribute("points"), ox, oy);
                        out.colliders.add(pts);
                        continue;
                    }

                    // rectangle object
                    if (!Float.isNaN(ow) && !Float.isNaN(oh) && ow > 0 && oh > 0) {
                        List<Vector2> rect = Arrays.asList(
                                new Vector2(ox, oy),
                                new Vector2(ox + ow, oy),
                                new Vector2(ox + ow, oy + oh),
                                new Vector2(ox, oy + oh)
                        );
                        out.colliders.add(rect);
                    }

                    // optional: by name/type, treat as spawn
                    String name = obj.getAttribute("name").toLowerCase(Locale.ROOT);
                    String type = obj.getAttribute("type").toLowerCase(Locale.ROOT);
                    if (groupName.contains("spawn") || name.contains("spawn") || type.contains("spawn")) {
                        out.spawnPoints.add(new Vector2(ox, oy));
                    }
                }
            }
            return out;
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse TMX: " + tmxRelativePath, e);
        }
    }

    private static float parseFloat(String s) {
        if (s == null || s.isEmpty()) return Float.NaN;
        return Float.parseFloat(s);
    }

    private static List<Vector2> parsePoints(String pointsAttr, float offsetX, float offsetY) {
        List<Vector2> out = new ArrayList<>();
        if (pointsAttr == null || pointsAttr.isEmpty()) return out;
        String[] pairs = pointsAttr.trim().split("\\s+");
        for (String p : pairs) {
            String[] xy = p.split(",");
            float x = Float.parseFloat(xy[0]) + offsetX;
            float y = Float.parseFloat(xy[1]) + offsetY;
            out.add(new Vector2(x, y));
        }
        return out;
    }
}

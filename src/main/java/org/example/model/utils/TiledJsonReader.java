package org.example.model.utils;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Base64;
import java.util.Arrays;
import java.util.List;
import java.util.zip.GZIPInputStream;
import java.util.zip.InflaterInputStream;

public final class TiledJsonReader {

    private TiledJsonReader() {}

    // Flip flags per Tiled specification (unsigned 32-bit)
    private static final long FLIP_H   = 0x80000000L;
    private static final long FLIP_V   = 0x40000000L;
    private static final long FLIP_D   = 0x20000000L;
    private static final long GID_MASK = 0x1FFFFFFFL;

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class MapJson {
        public int width, height, tilewidth, tileheight;
        public List<Layer> layers;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Layer {
        public String type, name, encoding, compression;

        // Raw 'data' straight from Tiled when it's a JSON array of numbers.
        // Use long[] so we can hold unsigned 32-bit values safely.
        public long[] data;

        // If the layer is an object layer, objects will be present.
        public List<TiledObject> objects;

        // Normalized tile IDs with flip bits stripped. Use this for logic.
        @JsonIgnore
        public int[] gids;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class TiledObject {
        public String name, type;
        public double x, y, width, height;
        public List<Point> polygon, polyline;
        public java.util.Map<String, Object> properties;
        public boolean ellipse; // Tiled sets "ellipse": true for ellipse objects
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Point {
        public double x, y;
    }

    /**
     * Load a Tiled JSON map from classpath and normalize tile layers.
     * Pass a classpath path, e.g. "/maps/Map1.json".
     */
    public static MapJson loadFromResource(String resourcePath) {
        try (InputStream in = TiledJsonReader.class.getResourceAsStream(resourcePath)) {
            if (in == null) {
                throw new IllegalArgumentException("Resource not found on classpath: " + resourcePath);
            }

            ObjectMapper om = new ObjectMapper();
            JsonNode root = om.readTree(in);
            MapJson m = om.treeToValue(root, MapJson.class);

            if (m.layers == null) return m;

            // Normalize tile layers: fill l.gids[] with flip bits stripped.
            JsonNode layersNode = root.get("layers");
            for (int idx = 0; idx < m.layers.size(); idx++) {
                Layer l = m.layers.get(idx);
                if (!"tilelayer".equals(l.type)) continue;

                // If JSON already had numeric array, it's in l.data (long[]).
                if (l.data != null) {
                    l.gids = new int[l.data.length];
                    for (int i = 0; i < l.data.length; i++) {
                        long raw = l.data[i];
                        l.gids[i] = (int) (raw & GID_MASK); // strip flip flags
                    }
                    continue;
                }

                // Otherwise, the "data" was encoded (array in JSON, CSV, or base64).
                JsonNode layerNode = layersNode.get(idx);
                JsonNode dataNode = layerNode.get("data");
                if (dataNode == null) {
                    // No data on this tile layer (rare), just skip
                    l.gids = new int[0];
                    continue;
                }

                if (dataNode.isArray()) {
                    int n = dataNode.size();
                    l.gids = new int[n];
                    for (int i = 0; i < n; i++) {
                        long raw = dataNode.get(i).asLong(); // may exceed signed int range
                        l.gids[i] = (int) (raw & GID_MASK);
                    }
                    continue;
                }

                // Encoded forms: CSV or base64 (+ optional zlib/gzip compression)
                String enc = l.encoding; // "csv" or "base64"
                String s = dataNode.asText();

                if ("csv".equals(enc)) {
                    String[] parts = s.split("\\s*,\\s*");
                    l.gids = new int[parts.length];
                    for (int i = 0; i < parts.length; i++) {
                        long raw = Long.parseUnsignedLong(parts[i]); // handle unsigned
                        l.gids[i] = (int) (raw & GID_MASK);
                    }
                } else if ("base64".equals(enc)) {
                    byte[] raw = Base64.getDecoder().decode(s.trim());
                    InputStream is = switch (l.compression == null ? "none" : l.compression) {
                        case "gzip" -> new GZIPInputStream(new ByteArrayInputStream(raw));
                        case "zlib" -> new InflaterInputStream(new ByteArrayInputStream(raw));
                        default     -> new ByteArrayInputStream(raw);
                    };
                    byte[] bytes = is.readAllBytes();
                    is.close();

                    int tiles = bytes.length / 4; // little-endian 32-bit per gid
                    l.gids = new int[tiles];
                    for (int i = 0, p = 0; i < tiles; i++, p += 4) {
                        long u = (bytes[p] & 0xFFL) |
                                ((bytes[p + 1] & 0xFFL) << 8) |
                                ((bytes[p + 2] & 0xFFL) << 16) |
                                ((bytes[p + 3] & 0xFFL) << 24);
                        l.gids[i] = (int) (u & GID_MASK);
                    }
                } else {
                    throw new IllegalStateException("Unknown tile layer encoding: " + enc);
                }
            }

            return m;
        } catch (Exception e) {
            throw new RuntimeException("Failed to load " + resourcePath, e);
        }
    }
}

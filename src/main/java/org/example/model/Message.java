package org.example.model;

import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Map;

public class Message {
    private Type type;
    private Map<String, Object> payload;
    private SocketChannel source;

    public Message() {}

    public Message(Type type, Map<String, Object> payload) {
        this.type = type;
        this.payload = payload;
    }

    public Message(Type type, String error) {
        if (type == Type.ERROR) {
            this.type = type;
            this.payload = new HashMap<>();
            payload.put("error", error);
        }
    }

    public Object getFromPayload(String key) {
        return payload.get(key);
    }

    public Type getType() {
        return type;
    }

    public Map<String, Object> getPayload() {
        return payload;
    }

    public static Message invalidArgument() {
        Map<String, Object> payload = new HashMap<>();
        payload.put("error", "invalid argument");
        return new Message(Type.ERROR, payload);
    }

    public static Message successWithToken(String token) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("status", "success");
        payload.put("token", token);
        return new Message(Type.SUCCESS, payload);
    }

    public static Message success() {
        return new Message(Type.SUCCESS, Map.of());
    }
    public static Message error(String error) {
        return new Message(Type.ERROR, Map.of("error", error));
    }

    public void setSource(SocketChannel source) {
        this.source = source;
    }

    public SocketChannel getSource() {
        return source;
    }
}

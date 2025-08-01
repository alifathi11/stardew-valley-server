package org.example.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Map;

public class Message {
    private Type type;
    private Map<String, Object> payload;

    @JsonIgnore
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


    public static Message success(Type type, String message) {
        return new Message(type, Map.of("status", "success", "content", message));
    }
    public static Message error(Type type, String error) {
        return new Message(type, Map.of("status", "error", "error", error));
    }


    @Override
    public String toString() {
        StringBuilder output = new StringBuilder();
        output.append("type: ").append(type.name());
        for (var entry : payload.entrySet()) {
            output.append(entry.getKey()).append(" : ").append(entry.getValue());
        }

        return output.toString();
    }

    public SocketChannel getSource() {
        return source;
    }

    public void setSource(SocketChannel source) {
        this.source = source;
    }
}

package org.example.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.model.message_center.Message;

public class MessageParser {
    private static final ObjectMapper mapper = new ObjectMapper();

    public static String toJson(Message msg) throws Exception {
        return mapper.writeValueAsString(msg);
    }

    public static Message fromJson(String json) throws Exception {
        return mapper.readValue(json, Message.class);
    }
}

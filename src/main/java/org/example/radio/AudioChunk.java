package org.example.radio;

import org.example.model.consts.Type;
import org.example.model.message_center.Message;

import java.util.Base64;
import java.util.Map;

public class AudioChunk {
    private final String sessionId;
    private final byte[] data;
    private final long timestamp;

    public AudioChunk(String sessionId, byte data[], long timestamp) {
        this.sessionId = sessionId;
        this.data = data;
        this.timestamp = timestamp;
    }

    public Message toMessage() {
        return new Message(Type.AUDIO_CHUNK, Map.of(
              "session_id", sessionId,
              "timestamp", timestamp,
              "data", Base64.getEncoder().encodeToString(data)
        ));
    }
}

package org.example.model;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class LobbyInvitation {
    private final String fromUser;
    private final String toUser;
    private final String lobbyId;
    private final String token;

    // global pending invitation set
    static final Set<LobbyInvitation> pending = ConcurrentHashMap.newKeySet();

    public LobbyInvitation(String fromUser,
                           String toUser,
                           String lobbyId,
                           String token) {
        this.fromUser = fromUser;
        this.toUser = toUser;
        this.lobbyId = lobbyId;
        this.token = token;
    }

    public String getFromUser() {
        return fromUser;
    }

    public String getLobbyId() {
        return lobbyId;
    }

    public String getToUser() {
        return toUser;
    }

    public static void addInvitation(LobbyInvitation invitation) {
        pending.add(invitation);
    }

    public String getToken() {
        return token;
    }
}

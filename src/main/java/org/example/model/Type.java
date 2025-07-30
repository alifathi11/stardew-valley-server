package org.example.model;

public enum Type {
    LOGIN,
    SIGNUP,
    SUCCESS,
    ERROR,
    NEW_GAME,
    LEADERBOARD,
    REACTION,
    CREATE_LOBBY,
    SEND_INVITATION,
    INVITATION,
    ACCEPT_INVITATION,
    TICK,
    CREATE_GAME,
    START_GAME,
    JOIN_LOBBY,
    CHOOSE_MAP,
    CHOOSE_NAME_GENDER,

    REQUEST_JOIN,   // user wanting to join -> server
    JOIN_REQUEST,   // server -> lobby host
    ACCEPT_JOIN,    // lobby host -> server
    JOIN_ACCEPTED,  // server -> user wanting to join
    REQUEST_LEAVE_LOBBY,
    LEAVE_LOBBY,
    ;
}

package org.example.model.user;

import java.time.Instant;

public class TokenInfo {
    private String username;
    private Instant expiry;

    public TokenInfo(String username, Instant expiry) {
        this.username = username;
        this.expiry = expiry;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Instant getExpiry() {
        return expiry;
    }

    public void setExpiry(Instant expiry) {
        this.expiry = expiry;
    }
}

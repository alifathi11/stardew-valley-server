package org.example.model.game_models;

import java.util.concurrent.atomic.AtomicInteger;

public class Vote {
    private VoteType type;
    private String targetUsername;
    private AtomicInteger positive;
    private AtomicInteger negative;

    public Vote(VoteType type, String username) {
        if (type == VoteType.FIRE_PLAYER) {
            this.type = type;
            this.targetUsername = username;
            this.positive = new AtomicInteger(1);
            this.negative = new AtomicInteger(0);
        }
    }

    public Vote(VoteType type) {
        if (type == VoteType.FORCE_TERMINATE) {
            this.type = type;
            this.positive = new AtomicInteger(1);
            this.negative = new AtomicInteger(0);
        }
    }

    public AtomicInteger getPositive() {
        return positive;
    }

    public AtomicInteger getNegative() {
        return negative;
    }

    public void incrementPositive() {
        this.positive.incrementAndGet();
    }

    public void incrementNegative() {
        this.negative.incrementAndGet();
    }

    public String getTargetUsername() {
        return targetUsername;
    }

    public VoteType getType() {
        return type;
    }

    public enum VoteType {
        FIRE_PLAYER,
        FORCE_TERMINATE,
        ;
    }
}

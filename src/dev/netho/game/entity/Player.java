package dev.netho.game.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

public class Player implements Serializable {

    private final String name;
    private int victories;
    private int defeats;
    private final LocalDateTime firstPlayedAt;

    public Player(String name, int victories, int defeats, LocalDateTime firstPlayedAt) {
        this.name = name;
        this.victories = victories;
        this.defeats = defeats;
        this.firstPlayedAt = firstPlayedAt;

    }

    public String getName() {
        return name;
    }

    public int getVictories() {
        return victories;
    }

    public int getDefeats() {
        return defeats;
    }

    public LocalDateTime getFirstPlayedAt() {
        return firstPlayedAt;
    }

    public void incrementVictories() {
        victories++;
    }

    public void incrementDefeats() {
        defeats++;
    }
}

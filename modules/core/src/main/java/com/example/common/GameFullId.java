package com.example.common;

import java.util.Objects;

public class GameFullId {
    private final String value; // e.g., "gameIdPlayerId"

    public static final int GAME_ID_LENGTH = 8;
    public static final int PLAYER_ID_LENGTH = 4;
    public static final int FULL_ID_LENGTH = GAME_ID_LENGTH + PLAYER_ID_LENGTH;


    public GameFullId(String value) {
        if (value == null || value.length() != FULL_ID_LENGTH) {
            // Or handle error more gracefully depending on expected inputs
            // For now, allowing null or incorrect length to be stored, consumers must be careful
            // throw new IllegalArgumentException("GameFullId must be " + FULL_ID_LENGTH + " characters long: " + value);
        }
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public GameId getGameId() {
        if (value == null || value.length() < GAME_ID_LENGTH) return new GameId(""); // Or throw
        return new GameId(value.substring(0, GAME_ID_LENGTH));
    }

    public GamePlayerId getPlayerId() {
         if (value == null || value.length() != FULL_ID_LENGTH) return new GamePlayerId(""); // Or throw
        return new GamePlayerId(value.substring(GAME_ID_LENGTH));
    }
    
    public static GameFullId of(GameId gameId, GamePlayerId playerId) {
        if (gameId == null || playerId == null) {
            // Or throw IllegalArgumentException
            return new GameFullId(null); 
        }
        return new GameFullId(gameId.getValue() + playerId.getValue());
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GameFullId that = (GameFullId) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        // Return the value directly as it's the string representation
        return value;
    }
}

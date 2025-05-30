package com.example.game;

import com.example.common.GameFullId;
import com.example.common.GameId;
import com.example.common.GamePlayerId;
import java.util.Objects;

public class PlayerRef {
    private final GameId gameId;
    private final GamePlayerId playerId;

    public PlayerRef(GameId gameId, GamePlayerId playerId) {
        this.gameId = Objects.requireNonNull(gameId);
        this.playerId = Objects.requireNonNull(playerId);
    }

    public GameId getGameId() { return gameId; }
    public GamePlayerId getPlayerId() { return playerId; }

    public static PlayerRef of(GameFullId fullId) {
        Objects.requireNonNull(fullId, "GameFullId cannot be null for PlayerRef.of");
        // Uses the new methods in GameFullId
        GameId gameId = fullId.getGameId();
        GamePlayerId playerId = fullId.getPlayerId();

        // Add validation if the extracted parts are empty, if necessary
        if (gameId.getValue() == null || gameId.getValue().isEmpty() || 
            playerId.getValue() == null || playerId.getValue().isEmpty()) {
            throw new IllegalArgumentException("Invalid GameFullId format, resulted in empty gameId or playerId: " + fullId.getValue());
        }
        return new PlayerRef(gameId, playerId);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PlayerRef playerRef = (PlayerRef) o;
        return gameId.equals(playerRef.gameId) && playerId.equals(playerRef.playerId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(gameId, playerId);
    }
}

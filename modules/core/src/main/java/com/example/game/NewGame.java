package com.example.game;

import com.example.common.GameId;

public class NewGame {
    private final Game sloppyGame;

    public NewGame(Game sloppyGame) {
        this.sloppyGame = sloppyGame;
    }

    public Game getSloppyGame() {
        return sloppyGame;
    }

    public Game withId(GameId id) {
        // Similar to ImportedGame.withId, needs a way to copy Game with a new ID
        return new Game(
            id, // new ID
            sloppyGame.getPlayers(),
            sloppyGame.getChessGame(),
            sloppyGame.getLoadClockHistoryFunction(),
            sloppyGame.getStatus(),
            sloppyGame.getDaysPerTurn(),
            sloppyGame.getBinaryMoveTimes(),
            sloppyGame.getMode(),
            sloppyGame.getBookmarks(),
            sloppyGame.getCreatedAt(),
            sloppyGame.getMovedAt(),
            sloppyGame.getMetadata()
        );
    }

    public NewGame start() {
        // Assumes Game.start() returns a new Game instance
        return new NewGame(sloppyGame.start());
    }
}

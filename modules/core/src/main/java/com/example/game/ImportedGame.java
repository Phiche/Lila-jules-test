package com.example.game;

import com.example.chess.Fen;
import com.example.common.GameId;
import java.util.Optional;

public class ImportedGame {
    private final Game sloppyGame;
    private final Optional<Fen.Full> initialFen;

    public ImportedGame(Game sloppyGame, Optional<Fen.Full> initialFen) {
        this.sloppyGame = sloppyGame;
        this.initialFen = initialFen == null ? Optional.empty() : initialFen;
    }

    public Game getSloppyGame() {
        return sloppyGame;
    }

    public Optional<Fen.Full> getInitialFen() {
        return initialFen;
    }

    public Game withId(GameId id) {
        // Game class needs a copy constructor or a builder/with-style methods
        // For now, assuming a way to update the ID. This is a placeholder.
        // This will likely be: new Game(id, sloppyGame.getPlayers(), sloppyGame.getChessGame(), ...all other fields...);
        // This requires Game to have getters for all its constructor parameters.
         return new Game(
            id, // new ID
            sloppyGame.getPlayers(),
            sloppyGame.getChessGame(),
            sloppyGame.getLoadClockHistoryFunction(), // Need a getter for this function in Game
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
}

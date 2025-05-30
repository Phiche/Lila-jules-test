package com.example.game;

import com.example.chess.ByColor;
import com.example.chess.ChessGame;
import com.example.chess.ClockHistory; // Added import
import com.example.chess.Fen; // Added import for Fen.Full
import com.example.chess.Mode;
import com.example.chess.Status;
import com.example.common.Days;
import com.example.common.GameId; // Assuming GameId is available

import java.time.Instant;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;

public class GameFactory {

    public static ImportedGame newImportedGame(
            ChessGame chessGame,
            ByColor<Player> players,
            Mode mode,
            Source source,
            Optional<PgnImport> pgnImport,
            Optional<Days> daysPerTurn,
            Set<GameRule> rules) {
        return new ImportedGame(
            newSloppy(chessGame, players, mode, source, pgnImport, daysPerTurn, rules),
            Optional.empty() // initialFen is not passed to newSloppy, so default for ImportedGame
        );
    }

    // Overload for when initialFen is explicitly provided for an imported game
    public static ImportedGame newImportedGameWithFen(
            ChessGame chessGame,
            ByColor<Player> players,
            Mode mode,
            Source source,
            Optional<PgnImport> pgnImport,
            Optional<Days> daysPerTurn,
            Set<GameRule> rules,
            Optional<Fen.Full> initialFen) { // Corrected type to Optional<Fen.Full>
        return new ImportedGame(
            newSloppy(chessGame, players, mode, source, pgnImport, daysPerTurn, rules),
            initialFen
        );
    }

    public static NewGame newGame(
            ChessGame chessGame,
            ByColor<Player> players,
            Mode mode,
            Source source,
            Optional<PgnImport> pgnImport,
            Optional<Days> daysPerTurn,
            Set<GameRule> rules) {
        return new NewGame(newSloppy(chessGame, players, mode, source, pgnImport, daysPerTurn, rules));
    }

    private static Game newSloppy(
            ChessGame chessGame,
            ByColor<Player> players,
            Mode mode,
            Source source,
            Optional<PgnImport> pgnImport,
            Optional<Days> daysPerTurn,
            Set<GameRule> rules) {

        Instant createdAt = Instant.now();
        // Ensure GameMetadata.newMetadata returns a GameMetadata instance
        // And that withPgnImport and withRules are available and return GameMetadata
        GameMetadata metadata = GameMetadata.newMetadata(source)
                                      .withPgnImport(pgnImport)
                                      .withRules(rules);

        return new Game(
                IdGenerator.uncheckedGame(), // Uses static method from IdGenerator interface
                players,
                chessGame,
                clock -> ClockHistory.SOME_EMPTY, // Default loadClockHistory function
                Status.CREATED,
                daysPerTurn,
                Optional.empty(), // binaryMoveTimes
                mode,
                0, // bookmarks
                createdAt,
                createdAt, // movedAt
                metadata
            );
    }
}

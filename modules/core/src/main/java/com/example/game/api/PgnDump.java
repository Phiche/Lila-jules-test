package com.example.game.api;
import com.example.chess.ByColor;
import com.example.chess.Fen;
import com.example.common.Pgn; // Placeholder
import com.example.common.Tags; // Placeholder
import com.example.common.TeamId; // Placeholder, create if not exists
import com.example.game.Game;
import com.example.game.PgnDumpFlags; // Created earlier
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public interface PgnDump {
    CompletableFuture<Pgn> apply(
        Game game,
        Optional<Fen.Full> initialFen,
        PgnDumpFlags flags,
        Optional<ByColor<TeamId>> teams
    );

    CompletableFuture<Tags> tags(
        Game game,
        Optional<Fen.Full> initialFen,
        Optional<Tags> importedTags,
        boolean withOpening,
        boolean withRating,
        Optional<ByColor<TeamId>> teams
    );
}

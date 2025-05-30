package com.example.game.api;
import com.example.common.GameId;
import com.example.common.UserId; // Assuming this is the correct UserId
import com.example.game.Game;
import com.example.game.Pov;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.UnaryOperator; // For Update<Game>

public interface GameProxy {
    CompletableFuture<Void> updateIfPresent(GameId gameId, UnaryOperator<Game> f);
    CompletableFuture<Optional<Game>> game(GameId gameId);
    CompletableFuture<Optional<Game>> gameIfPresent(GameId gameId);
    // UserIdOf typeclass equivalent: pass UserId directly or User object
    CompletableFuture<Optional<Pov>> pov(GameId gameId, UserId userId);
    CompletableFuture<List<Game>> upgradeIfPresent(List<Game> games);
    CompletableFuture<Void> flushIfPresent(GameId gameId);
}

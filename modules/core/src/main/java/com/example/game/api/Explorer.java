package com.example.game.api;
import com.example.common.GameId;
import com.example.game.Game;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public interface Explorer {
    CompletableFuture<Optional<Game>> apply(GameId id);
}

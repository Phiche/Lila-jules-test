package com.example.game;

import com.example.common.GameId;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface GameLightRepo {
    CompletableFuture<List<LightGame>> gamesFromSecondary(List<GameId> gameIds);
    CompletableFuture<List<LightGame>> gamesFromPrimary(List<GameId> gameIds);
}

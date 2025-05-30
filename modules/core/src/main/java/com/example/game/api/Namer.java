package com.example.game.api;

import com.example.game.Game;
import com.example.game.Player;
import com.example.lightuser.LightUser; // Assuming LightUser is in this package
import com.example.common.UserId; // Assuming this is the correct UserId

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public interface Namer {
    // LightUser.Getter is UserId => Fu[Option[LightUser]]
    // LightUser.GetterSync is UserId => Option[LightUser]
    CompletableFuture<String> gameVsText(
        Game game,
        boolean withRatings,
        Function<UserId, CompletableFuture<Optional<LightUser>>> lightUserGetter
    );

    CompletableFuture<String> playerText(
        Player player,
        boolean withRating,
        Function<UserId, CompletableFuture<Optional<LightUser>>> lightUserGetter
    );

    String gameVsTextBlocking(
        Game game,
        boolean withRatings,
        Function<UserId, Optional<LightUser>> lightUserGetterSync
    );

    String playerTextBlocking(
        Player player,
        boolean withRating,
        Function<UserId, Optional<LightUser>> lightUserGetterSync
    );
}

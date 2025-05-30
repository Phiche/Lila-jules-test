package com.example.game;

import com.example.common.GameId;
import java.util.function.Consumer;

@FunctionalInterface
public interface OnStart extends Consumer<GameId> {
    // Inherits void accept(GameId gameId);
}

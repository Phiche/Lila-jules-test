package com.example.game;

import com.example.chess.Color;
import com.example.common.GameId;
import com.example.common.UserId;
import java.util.Optional;

public class LightPov {
    private final LightGame game;
    private final Color color; // Player's color for this POV

    public LightPov(LightGame game, Color color) {
        this.game = game;
        this.color = color;
    }

    public LightGame getGame() { return game; }
    public Color getColor() { return color; }

    public GameId getGameId() {
        return game.getId();
    }

    public LightPlayer getPlayer() {
        return game.getPlayer(color);
    }

    public LightPlayer getOpponent() {
        // Assumes Color enum has unary_! method for opponent color
        return game.getPlayer(color.unary_!());
    }

    public static Optional<LightPov> of(LightGame game, UserId userId) {
        if (game == null || userId == null) return Optional.empty();
        return game.getPlayerByUserId(userId).map(p -> new LightPov(game, p.getColor()));
    }
}

package com.example.game.event;
import com.example.chess.ByColor;
import com.example.game.Game;
import com.example.perf.UserWithPerfs; // Placeholder
public class PerfsUpdate {
    private final Game game;
    private final ByColor<UserWithPerfs> perfs;
    public PerfsUpdate(Game game, ByColor<UserWithPerfs> perfs) {
        this.game = game;
        this.perfs = perfs;
    }
    public Game getGame() { return game; }
    public ByColor<UserWithPerfs> getPerfs() { return perfs; }
}

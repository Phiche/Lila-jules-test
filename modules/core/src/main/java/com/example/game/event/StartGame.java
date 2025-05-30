package com.example.game.event;
import com.example.game.Game;
public class StartGame {
    private final Game game;
    public StartGame(Game game) { this.game = game; }
    public Game getGame() { return game; }
}

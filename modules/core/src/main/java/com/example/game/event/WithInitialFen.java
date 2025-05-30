package com.example.game.event; // Or com.example.game if it's not strictly an event
import com.example.chess.Fen; // Placeholder
import com.example.game.Game;
import java.util.Optional;
public class WithInitialFen {
    private final Game game;
    private final Optional<Fen.Full> fen;
    public WithInitialFen(Game game, Optional<Fen.Full> fen) {
        this.game = game;
        this.fen = fen;
    }
    public Game getGame() { return game; }
    public Optional<Fen.Full> getFen() { return fen; }
}

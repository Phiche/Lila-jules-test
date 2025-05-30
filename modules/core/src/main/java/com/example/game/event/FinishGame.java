package com.example.game.event;
import com.example.chess.ByColor;
import com.example.game.Game;
import com.example.perf.UserWithPerfs; // Placeholder
import java.util.Optional;
public class FinishGame {
    private final Game game;
    private final ByColor<Optional<UserWithPerfs>> usersBeforeGame; // Optional for each player
    public FinishGame(Game game, ByColor<Optional<UserWithPerfs>> usersBeforeGame) {
        this.game = game;
        this.usersBeforeGame = usersBeforeGame;
    }
    public Game getGame() { return game; }
    public ByColor<Optional<UserWithPerfs>> getUsersBeforeGame() { return usersBeforeGame; }
}

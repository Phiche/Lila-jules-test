package com.example.game.event; // Grouping event classes in a subpackage
import com.example.common.GameId;
public class GameStart {
    private final GameId id;
    public GameStart(GameId id) { this.id = id; }
    public GameId getId() { return id; }
}

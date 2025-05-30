package com.example.game.api;
import com.example.game.Game;
import java.util.List; // Vector replaced by List
import java.util.concurrent.CompletableFuture;

public interface UciMemo {
    CompletableFuture<List<String>> get(Game game);
    CompletableFuture<String> sign(Game game);
}

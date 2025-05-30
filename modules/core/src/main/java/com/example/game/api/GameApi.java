package com.example.game.api; // Grouping API interfaces

import com.example.common.GameId;
import com.example.common.Pair; // For Pair type
import com.example.common.UserId;
import com.example.game.Pov; // Assuming Pov is in com.example.game
import com.example.game.Source; // Assuming Source is in com.example.game
import com.example.common.JsonData; // Placeholder for JsObject

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public interface GameApi {
    CompletableFuture<Pair<Optional<Source>, List<UserId>>> getSourceAndUserIds(GameId id);
    CompletableFuture<Void> incBookmarks(GameId id, int by);
    // computeMoveTimes's Game argument will be com.example.game.Game
    CompletableFuture<Optional<List<com.example.chess.Centis>>> computeMoveTimes(com.example.game.Game g, com.example.chess.Color color);
    boolean analysable(com.example.game.Game g);
    CompletableFuture<Integer> nbPlaying(UserId userId);
    Optional<JsonData> anonCookieJson(Pov pov);
}
// Define a simple Pair for getSourceAndUserIds if not already globally available
// For now, assuming a com.example.common.Pair<K,V> exists.

package com.example.game;

import com.example.chess.Color;
import com.example.common.JsonData; // Using the placeholder
import java.util.Optional;

public interface Event {
    String getType();
    JsonData getData(); // Placeholder for JsValue

    default Optional<Color> getOnlyColor() { return Optional.empty(); }
    default boolean isOwner() { return false; }
    default boolean isWatcher() { return false; }
    default boolean isTroll() { return false; }
    default Optional<Color> getMoveBy() { return Optional.empty(); }
}

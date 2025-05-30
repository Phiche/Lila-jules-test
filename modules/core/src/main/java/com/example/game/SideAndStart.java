package com.example.game;

import com.example.chess.Color;
import com.example.chess.Ply;
import java.util.Objects;

public class SideAndStart {
    private final Color color;
    private final Ply startedAtPly;

    public SideAndStart(Color color, Ply startedAtPly) {
        this.color = Objects.requireNonNull(color);
        this.startedAtPly = Objects.requireNonNull(startedAtPly);
    }

    public Color getColor() { return color; }
    public Ply getStartedAtPly() { return startedAtPly; }

    public Color getStartColor() {
        return startedAtPly.getTurn(); // Assumes Ply.getTurn() gives the color to move
    }
}

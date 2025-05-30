package com.example.captcha;

import com.example.common.GameId;
import com.example.common.BoardFen;
import com.example.common.Color;

import java.util.List;
import java.util.Map;

public class Captcha {

    private final GameId gameId;
    private final BoardFen fen;
    private final Color color;
    private final List<String> solutions; // Replace NonEmptyList with List
    private final Map<String, String> moves;

    public Captcha(GameId gameId, BoardFen fen, Color color, List<String> solutions, Map<String, String> moves) {
        if (solutions == null || solutions.isEmpty()) {
            throw new IllegalArgumentException("Solutions cannot be null or empty");
        }
        this.gameId = gameId;
        this.fen = fen;
        this.color = color;
        this.solutions = solutions;
        this.moves = moves;
    }

    public GameId getGameId() {
        return gameId;
    }

    public BoardFen getFen() {
        return fen;
    }

    public Color getColor() {
        return color;
    }

    public List<String> getSolutions() {
        return solutions;
    }

    public Map<String, String> getMoves() {
        return moves;
    }
}

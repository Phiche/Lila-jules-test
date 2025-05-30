package com.example.game.api;
import com.example.chess.Fen;
import com.example.chess.SanStr;
import com.example.chess.Variant;
import com.example.common.GameId;
import com.example.chess.Division; // Placeholder, create if not exists
import java.util.List; // Vector replaced by List
import java.util.Optional;

public interface Divider {
    Division apply(GameId id, List<SanStr> sans, Variant variant, Optional<Fen.Full> initialFen);
}

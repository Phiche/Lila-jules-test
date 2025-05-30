package com.example.game;

import com.example.chess.Color;
import com.example.chess.Status;
import com.example.chess.Variant;
import com.example.common.GameId;
import com.example.common.UserId;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class LightGame {
    private final GameId id;
    private final LightPlayer whitePlayer;
    private final LightPlayer blackPlayer;
    private final Status status;
    private final Optional<Color> win; // Winning color
    private final Variant variant;

    public LightGame(GameId id, LightPlayer whitePlayer, LightPlayer blackPlayer, Status status, Optional<Color> win, Variant variant) {
        this.id = id;
        this.whitePlayer = whitePlayer;
        this.blackPlayer = blackPlayer;
        this.status = status;
        this.win = win == null ? Optional.empty() : win;
        this.variant = variant;
    }

    public GameId getId() { return id; }
    public LightPlayer getWhitePlayer() { return whitePlayer; }
    public LightPlayer getBlackPlayer() { return blackPlayer; }
    public Status getStatus() { return status; }
    public Optional<Color> getWin() { return win; }
    public Variant getVariant() { return variant; }

    public boolean isPlayable() {
        // Assuming Status enum order where CREATED/STARTED are less than ABORTED
        return status.ordinal() < Status.ABORTED.ordinal(); 
    }

    public LightPlayer getPlayer(Color color) {
        return (color == Color.WHITE) ? whitePlayer : blackPlayer;
    }

    public List<LightPlayer> getPlayers() {
        return Arrays.asList(whitePlayer, blackPlayer);
    }

    public Optional<LightPlayer> getPlayerByUserId(UserId userId) {
        if (userId == null) return Optional.empty();
        if (whitePlayer.getUserId().map(id -> id.equals(userId)).orElse(false)) {
            return Optional.of(whitePlayer);
        }
        if (blackPlayer.getUserId().map(id -> id.equals(userId)).orElse(false)) {
            return Optional.of(blackPlayer);
        }
        return Optional.empty();
    }

    public boolean isFinished() {
        // Assuming MATE is a status indicating a finished game, and enum order is sensible
        return status.isGreaterThanOrEqual(Status.MATE); 
    }

    public Optional<LightPlayer> getWinner() {
        return win.map(this::getPlayer);
    }

    public Optional<UserId> getWinnerUserId() {
        return getWinner().flatMap(LightPlayer::getUserId);
    }
}

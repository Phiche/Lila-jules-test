package com.example.game;

import com.example.chess.Board; // Added import
import com.example.chess.ByColor;
import com.example.chess.Centis;
import com.example.chess.ChessGame;
import com.example.chess.Clock;
import com.example.chess.Color;
import com.example.chess.CorrespondenceClock;
import com.example.chess.History; // Added import
import com.example.chess.IntRating;
import com.example.chess.Mode;
import com.example.chess.Opening;
import com.example.chess.Outcome;
import com.example.chess.Ply;
import com.example.chess.SanStr;
import com.example.chess.Speed;
import com.example.chess.Status;
import com.example.chess.Uci;
import com.example.chess.Variant;
import com.example.common.Days;
import com.example.common.GameFullId; // Added for new methods
import com.example.common.GameId;
import com.example.common.GamePlayerId; // Added for getPlayerById
import com.example.common.UserId; // Added for hasUserId, hasUserIds
import com.example.perf.PerfKey;
import com.example.user.User; // Added for new methods
// import com.example.user.User; // User class placeholder needed

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
// import java.util.Vector; // Using Vector as a placeholder for clock states, might change - Not used here
import java.util.function.Function;
import java.util.stream.Collectors;

public class Game {

    private final GameId id;
    private final ByColor<Player> players;
    private final ChessGame chessGame; // Renamed from 'chess' to avoid conflict with package
    private final Function<Clock, Optional<ClockHistory>> loadClockHistory;
    private final Status status;
    private final Optional<Days> daysPerTurn;
    private final Optional<byte[]> binaryMoveTimes;
    private final Mode mode;
    private final int bookmarks;
    private final Instant createdAt;
    private final Instant movedAt;
    private final GameMetadata metadata;

    // Lazily initialized
    private transient Optional<ClockHistory> clockHistoryCache = null;


    public Game(
            GameId id,
            ByColor<Player> players,
            ChessGame chessGame,
            Function<Clock, Optional<ClockHistory>> loadClockHistory,
            Status status,
            Optional<Days> daysPerTurn,
            Optional<byte[]> binaryMoveTimes,
            Mode mode,
            int bookmarks,
            Instant createdAt,
            Instant movedAt,
            GameMetadata metadata) {
        this.id = id;
        this.players = players;
        this.chessGame = chessGame;
        this.loadClockHistory = loadClockHistory;
        this.status = status;
        this.daysPerTurn = daysPerTurn == null ? Optional.empty() : daysPerTurn;
        this.binaryMoveTimes = binaryMoveTimes == null ? Optional.empty() : binaryMoveTimes;
        this.mode = mode == null ? Mode.defaultMode() : mode; // Assumes Mode.defaultMode() exists
        this.bookmarks = bookmarks;
        this.createdAt = createdAt == null ? Instant.now() : createdAt;
        this.movedAt = movedAt == null ? Instant.now() : movedAt;
        this.metadata = metadata == null ? GameMetadata.EMPTY : metadata;
    }

    // --- Getters for direct fields ---
    public GameId getId() { return id; }
    public ByColor<Player> getPlayers() { return players; }
    public ChessGame getChessGame() { return chessGame; }
    public Status getStatus() { return status; }
    public Optional<Days> getDaysPerTurn() { return daysPerTurn; }
    public Optional<byte[]> getBinaryMoveTimes() { return binaryMoveTimes; }
    public Mode getMode() { return mode; }
    public int getBookmarks() { return bookmarks; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getMovedAt() { return movedAt; }
    public GameMetadata getMetadata() { return metadata; }

    // --- Delegated getters (from Scala 'export') ---
    public Board getPosition() { return chessGame.getPosition(); }
    public Ply getPly() { return chessGame.getPly(); }
    public Optional<Clock> getClock() { return chessGame.getClock(); }
    public List<SanStr> getSans() { return chessGame.getSans(); }
    public Ply getStartedAtPly() { return chessGame.getStartedAtPly(); }
    public Color getTurnColor() { return chessGame.getPlayer(); } // Renamed from 'player' to avoid clash
    public History getHistory() { return chessGame.getHistory(); }
    public Variant getVariant() { return chessGame.getVariant(); }

    public Optional<com.example.common.TourId> getTournamentId() { return metadata.getTournamentId(); }
    public Optional<com.example.common.SimulId> getSimulId() { return metadata.getSimulId(); }
    public Optional<com.example.common.SwissId> getSwissId() { return metadata.getSwissId(); }
    public GameDrawOffers getDrawOffers() { return metadata.getDrawOffers(); }
    public Optional<Source> getSource() { return metadata.getSource(); }
    public Optional<PgnImport> getPgnImport() { return metadata.getPgnImport(); }
    public boolean hasRule(GameRule rule) { return metadata.hasRule(rule); } // Simplified

    public Player getWhitePlayer() { return players.getWhite(); }
    public Player getBlackPlayer() { return players.getBlack(); }
    public Player getPlayer(Color color) { return players.get(color); }


    // --- Lazy ClockHistory ---
    public Optional<ClockHistory> getClockHistory() {
        if (clockHistoryCache == null) {
            // Ensure loadClockHistory is not null before invoking, or handle NPE if it can be null.
            // The current constructor does not prevent loadClockHistory from being null.
            if (this.loadClockHistory == null) {
                 clockHistoryCache = Optional.empty();
            } else {
                clockHistoryCache = getClock().flatMap(loadClockHistory);
            }
        }
        return clockHistoryCache;
    }
    
    // --- toString ---
    @Override
    public String toString() {
        // Assuming GameId has a meaningful getValue() or similar for string representation
        return "Game(" + (id != null ? id.getValue() : "null") + ")";
    }

    // --- Player retrieval and checks ---

    // Placeholder for User class, assuming it has getId() returning UserId
    public Optional<Player> getPlayer(com.example.user.User user) {
        if (user == null) return Optional.empty();
        return players.find(p -> p.getUserId().map(uid -> uid.equals(user.getId())).orElse(false));
    }

    public Optional<Player> getOpponentOf(com.example.user.User user) {
        return getPlayer(user).map(this::getOpponent);
    }

    /**
     * Gets the player whose turn it is.
     */
    public Player getCurrentTurnPlayer() {
        return players.get(getTurnColor());
    }

    public Optional<Player> getPlayerById(GamePlayerId playerId) {
        if (playerId == null) return Optional.empty();
        return players.find(p -> p.getId().equals(playerId));
    }

    public boolean hasUserIds(UserId userId1, UserId userId2) {
        return hasUserId(userId1) && hasUserId(userId2);
    }

    public boolean hasUserId(UserId userId) {
        if (userId == null) return false;
        return players.exists(p -> p.getUserId().map(uid -> uid.equals(userId)).orElse(false));
    }

    public ByColor<Optional<UserId>> getUserIdPair() {
        return players.map(Player::getUserId);
    }

    public Player getOpponent(Player p) {
        if (p == null) throw new IllegalArgumentException("Player cannot be null");
        return getOpponent(p.getColor());
    }

    public Player getOpponent(Color c) {
        if (c == null) throw new IllegalArgumentException("Color cannot be null");
        return getPlayer(c.unary_!()); // Using the unary_! operator defined in Color enum
    }

    // --- Game state and ID methods ---

    public Color getNaturalOrientation() {
        if (getVariant().isRacingKings()) { // Assuming isRacingKings on Variant enum
            return Color.WHITE;
        } else {
            // Scala: Color.fromWhite(players.reduce(_.before(_)))
            // This implies Player has a 'before' method and Color has 'fromWhite(boolean)'
            // Player.before is more complex, for now, let's assume white is default if not racing kings
            // This needs to be revisited when Player.before is fully translated and Color.fromWhite is clear
            return players.getWhite().before(players.getBlack()) ? Color.WHITE : Color.BLACK; // Placeholder logic
        }
    }

    public boolean isTurnOf(Player p) {
        if (p == null) return false;
        return p.equals(getCurrentTurnPlayer());
    }

    public boolean isTurnOf(Color c) {
        if (c == null) return false;
        return c == getTurnColor();
    }

    public boolean isTurnOf(com.example.user.User u) {
        if (u == null) return false;
        return getPlayer(u).map(this::isTurnOf).orElse(false);
    }

    /**
     * Number of plies played since the game started (ply at start might not be 0).
     */
    public int getPlayedTurns() {
        return getPly().getValue() - getStartedAtPly().getValue();
    }

    public Optional<Color> getFlaggedPlayerColor() {
        return (getStatus() == Status.OUTOFTIME) ? Optional.of(getTurnColor()) : Optional.empty();
    }

    public Optional<GameFullId> getFullIdOf(Player player) {
        if (player == null) return Optional.empty();
        // Assuming players is ByColor<Player> and Player has getId() returning GamePlayerId
        if (players.getWhite().equals(player) || players.getBlack().equals(player)) {
            return Optional.of(new GameFullId(id.getValue() + player.getId().getValue()));
        }
        return Optional.empty();
    }

    public GameFullId getFullIdOf(Color color) {
        return new GameFullId(id.getValue() + getPlayer(color).getId().getValue());
    }

    public ByColor<GameFullId> getFullIds() {
        return new ByColor<>(getFullIdOf(Color.WHITE), getFullIdOf(Color.BLACK));
    }

    // --- Game properties and status methods ---

    public boolean isTournament() {
        return getTournamentId().isPresent();
    }

    public boolean isSimul() {
        return getSimulId().isPresent();
    }

    public boolean isSwiss() {
        return getSwissId().isPresent();
    }

    public boolean isMandatory() {
        return isTournament() || isSimul() || isSwiss();
    }

    public boolean nonMandatory() {
        return !isMandatory();
    }

    public boolean canTakebackOrAddTime() {
        return !isMandatory();
    }

    public Optional<Integer> getDurationSeconds() {
        long seconds = getMovedAt().getEpochSecond() - getCreatedAt().getEpochSecond();
        if (seconds < 60 * 60 * 12) { // Max 12 hours
            return Optional.of((int) seconds);
        }
        return Optional.empty();
    }

    public Optional<List<Centis>> getBothClockStates() {
        // In Scala: clockHistory.map(_.bothClockStates(startColor))
        // Assuming ClockHistory.getBothClockStates(Color) returns List<Centis>
        // And Game.getStartColor() returns the starting color of the game.
        return getClockHistory().map(ch -> ch.getBothClockStates(getStartColor()));
    }

    public List<SanStr> getSansOf(Color color) {
        List<SanStr> allSans = getSans();
        List<SanStr> playerSans = new java.util.ArrayList<>();
        int pivot = (color == getStartColor()) ? 0 : 1;
        for (int i = 0; i < allSans.size(); i++) {
            if ((i % 2) == pivot) {
                playerSans.add(allSans.get(i));
            }
        }
        return Collections.unmodifiableList(playerSans);
    }

    public Optional<String> getLastMoveKeys() {
        return getHistory().getLastMove().map(uci -> {
            if (uci instanceof Uci.Drop) {
                // Assuming Uci.Drop has getSquare() and Square has getKey()
                return ((Uci.Drop) uci).getSquare().getKey() + ((Uci.Drop) uci).getSquare().getKey();
            } else if (uci instanceof Uci.Move) {
                // Assuming Uci.Move has getKeys()
                return ((Uci.Move) uci).getKeys();
            }
            return null; // Should not happen if Uci is sealed to Move and Drop
        });
    }

    // --- Helper for startColor (used in getBothClockStates and getSansOf) ---
    /**
     * Gets the color of the player who made the first move.
     * Based on startedAtPly.
     */
    public Color getStartColor() {
        return getStartedAtPly().getTurn();
    }

    // --- Game state modification and status checks ---

    public Game updatePlayer(Color color, Function<Player, Player> f) {
        ByColor<Player> updatedPlayers = getPlayers().update(color, f);
        return new Game(
                id, updatedPlayers, chessGame, loadClockHistory, status, daysPerTurn,
                binaryMoveTimes, mode, bookmarks, createdAt, movedAt, metadata
        );
    }

    public Game start() {
        if (isStarted()) {
            return this;
        }
        // In Scala: mode = Mode(mode.rated && userIds.distinct.size == 2)
        // This implies userIds needs to be available here.
        // For now, keeping the existing mode if it's already rated, or making it casual.
        // The logic for determining if a game *becomes* rated at start needs careful porting of userIds and distinct check.
        boolean newRated = getMode().isRated() && (getUserIds().stream().distinct().count() == 2);

        return new Game(
                id, players, chessGame, loadClockHistory, Status.STARTED, daysPerTurn,
                binaryMoveTimes, new Mode(newRated), bookmarks, createdAt, movedAt, metadata
        );
    }

    public Optional<CorrespondenceClock> getCorrespondenceClock() {
        return getDaysPerTurn().map(d -> new CorrespondenceClock(d.getValue(), getTurnColor(), getMovedAt()));
    }

    public Optional<CorrespondenceClock> getPlayableCorrespondenceClock() {
        return isPlayable() ? getCorrespondenceClock() : Optional.empty();
    }

    public PerfKey getPerfKey() {
        // Assuming PerfKey constructor takes Variant and Speed
        // Using name() for enums to get the string representation
        return new PerfKey(getVariant().name() + "_" + getSpeed().name()); // Simplified construction
    }

    public Variant getRatingVariant() {
        if (isTournament() && getVariant().isFromPosition()) { // Assuming Variant.isFromPosition()
            return Variant.STANDARD;
        }
        return getVariant();
    }

    public boolean isStarted() {
        return getStatus().isGreaterThanOrEqual(Status.STARTED); // Uses Status.isGreaterThanOrEqual
    }

    public boolean isAborted() {
        return getStatus() == Status.ABORTED;
    }

    public Game abort() {
        if (isAborted()) return this;
        return new Game(
                id, players, chessGame, loadClockHistory, Status.ABORTED, daysPerTurn,
                binaryMoveTimes, mode, bookmarks, createdAt, movedAt, metadata
        );
    }

    public boolean isPlayable() {
        // Scala: status < Status.Aborted && !sourceIs(_.Import)
        // Assuming Status enum order means lower ordinal is "less than"
        // And Source enum has an IMPORT value
        boolean isImport = getSource().map(s -> s == com.example.game.Source.IMPORT).orElse(false);
        return getStatus().ordinal() < Status.ABORTED.ordinal() && !isImport;
    }
    
    // Helper method to get userIds for the start() method, similar to Scala's userIds
    public List<UserId> getUserIds() {
        List<UserId> ids = new java.util.ArrayList<>();
        getPlayers().getWhite().getUserId().ifPresent(ids::add);
        getPlayers().getBlack().getUserId().ifPresent(ids::add);
        return Collections.unmodifiableList(ids);
    }
    // More methods will be added in subsequent steps.
}

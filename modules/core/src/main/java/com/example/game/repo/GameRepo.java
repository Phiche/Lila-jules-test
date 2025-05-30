package com.example.game.repo; // Grouping repository interfaces

import com.example.chess.Color;
import com.example.chess.Fen;
import com.example.chess.Status;
import com.example.common.GameId;
import com.example.common.Pair; // For getSourceAndUserIds
import com.example.common.UserId;
import com.example.game.Game; // Main game object
import com.example.game.Source;
import com.example.game.event.WithInitialFen; // From game.event package
import com.example.perf.PerfKey; // From perf package
import com.example.user.User; // Placeholder for user

// import reactivemongo.api.bson.collection.BSONCollection; // Scala/ReactiveMongo specific
// import reactivemongo.api.bson.BSONDocumentHandler; // Scala/ReactiveMongo specific
// import reactivemongo.api.bson.BSONHandler; // Scala/ReactiveMongo specific
// import reactivemongo.akkastream.AkkaStreamCursor; // Scala/ReactiveMongo specific
import com.example.db.BsonCollectionPlaceholder; // New placeholder
import com.example.db.BsonDocumentHandlerPlaceholder; // New placeholder
import com.example.db.BsonHandlerPlaceholder; // New placeholder
import com.example.db.AkkaStreamCursorPlaceholder; // New placeholder
import com.example.game.GameLightRepo; // For the light field


import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public abstract class GameRepo { // Changed to abstract class to hold BSONCollection
    protected final BsonCollectionPlaceholder coll; // Placeholder type
    public final GameLightRepo light; // Already defined interface

    protected GameRepo(BsonCollectionPlaceholder coll, GameLightRepo light) {
        this.coll = coll;
        this.light = light;
    }

    // Abstract methods to be implemented by concrete repo, mirroring the Scala trait
    // These would need actual BSONHandlers for Game and Status if using a similar pattern in Java
    // For now, just declaring them.
    // protected abstract BsonDocumentHandlerPlaceholder<Game> gameHandler();
    // protected abstract BsonHandlerPlaceholder<Status, ?> statusHandler(); // BSONHandler might be more complex

    public abstract CompletableFuture<Optional<Game>> game(GameId gameId);
    public abstract CompletableFuture<Optional<Game>> gameFromSecondary(GameId gameId);
    public abstract CompletableFuture<List<Game>> gamesFromSecondary(List<GameId> gameIds);
    public abstract CompletableFuture<List<Optional<Game>>> gameOptionsFromSecondary(List<GameId> gameIds); // Assuming Optional<Game> for list elements
    public abstract CompletableFuture<Pair<Optional<Source>, List<UserId>>> getSourceAndUserIds(GameId id);
    public abstract CompletableFuture<Optional<Fen.Full>> initialFen(GameId gameId);
    public abstract CompletableFuture<Optional<Fen.Full>> initialFen(Game game);
    public abstract CompletableFuture<WithInitialFen> withInitialFen(Game game);
    public abstract CompletableFuture<Optional<WithInitialFen>> gameWithInitialFen(GameId gameId);
    public abstract CompletableFuture<Boolean> isAnalysed(Game game);
    public abstract CompletableFuture<Void> insertDenormalized(Game g, Optional<Fen.Full> initialFen);
    public abstract CompletableFuture<List<Game>> recentAnalysableGamesByUserId(UserId userId, int nb);
    public abstract CompletableFuture<List<Game>> lastGamesBetween(User u1, User u2, Instant since, int nb);
    public abstract CompletableFuture<Optional<Game>> analysed(GameId id);
    public abstract CompletableFuture<Void> setAnalysed(GameId id, boolean v);
    public abstract CompletableFuture<Void> finish(GameId id, Optional<Color> winnerColor, Optional<UserId> winnerId, Status status);
    public abstract CompletableFuture<Void> remove(GameId id);
    public abstract CompletableFuture<Integer> countWhereUserTurn(UserId userId);
    public abstract AkkaStreamCursorPlaceholder<Game> sortedCursor(User user, PerfKey pk);
}

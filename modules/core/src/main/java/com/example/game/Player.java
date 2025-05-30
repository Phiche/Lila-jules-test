package com.example.game;

import com.example.chess.Color;
import com.example.chess.IntRating;
import com.example.chess.IntRatingDiff;
import com.example.chess.PlayerName;
import com.example.chess.Ply;
import com.example.chess.RatingProvisional;
import com.example.common.GamePlayerId;
import com.example.common.UserId;
// Assuming com.example.perf.Perf and com.example.user.WithPerf would be defined elsewhere
// For now, methods requiring them in NewPlayerFactory might be commented out or adapted.

import java.util.Objects;
import java.util.Optional;

public class Player {
    private final GamePlayerId id;
    private final Color color;
    private final Optional<Integer> aiLevel;
    private final Optional<Boolean> isWinner;
    private final boolean isOfferingDraw;
    private final Ply proposeTakebackAt;
    private final Optional<UserId> userId;
    private final Optional<IntRating> rating;
    private final Optional<IntRatingDiff> ratingDiff;
    private final RatingProvisional provisional;
    private final Blurs blurs;
    private final boolean berserk;
    private final boolean blindfold;
    private final Optional<PlayerName> name;

    public Player(
            GamePlayerId id, Color color, Optional<Integer> aiLevel, Optional<Boolean> isWinner,
            boolean isOfferingDraw, Ply proposeTakebackAt, Optional<UserId> userId,
            Optional<IntRating> rating, Optional<IntRatingDiff> ratingDiff,
            RatingProvisional provisional, Blurs blurs, boolean berserk, boolean blindfold,
            Optional<PlayerName> name) {
        this.id = Objects.requireNonNull(id);
        this.color = Objects.requireNonNull(color);
        this.aiLevel = aiLevel == null ? Optional.empty() : aiLevel;
        this.isWinner = isWinner == null ? Optional.empty() : isWinner;
        this.isOfferingDraw = isOfferingDraw;
        this.proposeTakebackAt = proposeTakebackAt == null ? Ply.initial() : proposeTakebackAt;
        this.userId = userId == null ? Optional.empty() : userId;
        this.rating = rating == null ? Optional.empty() : rating;
        this.ratingDiff = ratingDiff == null ? Optional.empty() : ratingDiff;
        this.provisional = provisional == null ? RatingProvisional.NO : provisional;
        this.blurs = blurs == null ? new Blurs(0L) : blurs;
        this.berserk = berserk;
        this.blindfold = blindfold;
        this.name = name == null ? Optional.empty() : name;
    }

    // Getters
    public GamePlayerId getId() { return id; }
    public Color getColor() { return color; }
    public Optional<Integer> getAiLevel() { return aiLevel; }
    public Optional<Boolean> isWinner() { return isWinner; } // Renamed from getIsWinner for boolean convention
    public boolean isOfferingDraw() { return isOfferingDraw; }
    public Ply getProposeTakebackAt() { return proposeTakebackAt; }
    public Optional<UserId> getUserId() { return userId; }
    public Optional<IntRating> getRating() { return rating; }
    public Optional<IntRatingDiff> getRatingDiff() { return ratingDiff; }
    public RatingProvisional getProvisional() { return provisional; }
    public Blurs getBlurs() { return blurs; }
    public boolean isBerserk() { return berserk; }
    public boolean isBlindfold() { return blindfold; }
    public Optional<PlayerName> getName() { return name; }

    // Methods
    public boolean isAi() {
        return aiLevel.isPresent();
    }

    public boolean hasUser() {
        return userId.isPresent();
    }

    public boolean isUser(UserId u) {
        return userId.map(uid -> uid.equals(u)).orElse(false);
    }

    public Player removeTakebackProposition() {
        return new Player(id, color, aiLevel, isWinner, isOfferingDraw, Ply.initial(), userId, rating, ratingDiff, provisional, blurs, berserk, blindfold, name);
    }

    public boolean isProposingTakeback() {
        // Assuming Ply.initial() gives a ply with value 0 or a specific non-positive value.
        // And Ply.getValue() returns the ply number.
        return proposeTakebackAt.getValue() > Ply.initial().getValue();
    }

    public boolean before(Player other) {
        if (rating.isPresent() && other.rating.isPresent()) {
            if (!rating.get().equals(other.rating.get())) {
                return rating.get().getValue() > other.rating.get().getValue();
            }
        } else if (rating.isPresent()) {
            return true; // This player has a rating, the other doesn't, so this is "before"
        } else if (other.rating.isPresent()) {
            return false; // The other player has a rating, this one doesn't
        }
        // If ratings are equal or both absent, compare by ID
        return id.getValue().compareTo(other.id.getValue()) < 0;
    }

    public Optional<IntRating> ratingAfter() {
        return rating.flatMap(r -> ratingDiff.map(rd -> new IntRating(r.getValue() + rd.getValue())));
    }

    public Optional<IntRating> stableRating() {
        return provisional == RatingProvisional.NO ? rating : Optional.empty();
    }

    public Optional<IntRating> stableRatingAfter() {
        // Corrected to use stableRating() as the base for calculation
        return stableRating().flatMap(r -> ratingDiff.map(rd -> new IntRating(r.getValue() + rd.getValue())));
    }

    public LightPlayer light() {
        return new LightPlayer(color, aiLevel, userId, rating, ratingDiff, provisional, berserk);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Player player = (Player) o;
        // Based on original Scala, equality is often by ID for entities
        return id.equals(player.id);
        // If color also defines unique player in a game context (e.g. playerWhite, playerBlack):
        // return id.equals(player.id) && color == player.color;
        // The provided snippet used `userId` as well, which might be too restrictive if ID is already unique.
        // Let's stick to the provided snippet's logic:
        // return id.equals(player.id) && Objects.equals(userId, player.userId);
        // Re-evaluating: A player in a game is uniquely identified by their ID within that game.
        // UserId might not always be present (AI, anonymous). Color is fundamental.
        // return id.equals(player.id) && color == player.color;
        // The original code was: id == that.id && userId == that.userId
        // This implies that (GamePlayerId, Optional<UserId>) is the key.
        // It's unusual for a game-specific player ID not to be unique on its own.
        // Let's use the provided equals logic:
        return id.equals(player.id) && Objects.equals(userId, player.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, userId);
    }
}

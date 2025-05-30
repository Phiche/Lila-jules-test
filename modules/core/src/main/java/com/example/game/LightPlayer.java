package com.example.game;

import com.example.chess.Color;
import com.example.chess.IntRating;
import com.example.chess.IntRatingDiff;
import com.example.chess.RatingProvisional;
import com.example.common.UserId;
import java.util.Optional;

public class LightPlayer {
    private final Color color;
    private final Optional<Integer> aiLevel;
    private final Optional<UserId> userId;
    private final Optional<IntRating> rating;
    private final Optional<IntRatingDiff> ratingDiff;
    private final RatingProvisional provisional;
    private final boolean berserk;

    public LightPlayer(
            Color color,
            Optional<Integer> aiLevel,
            Optional<UserId> userId,
            Optional<IntRating> rating,
            Optional<IntRatingDiff> ratingDiff,
            RatingProvisional provisional,
            boolean berserk) {
        this.color = color;
        this.aiLevel = aiLevel == null ? Optional.empty() : aiLevel;
        this.userId = userId == null ? Optional.empty() : userId;
        this.rating = rating == null ? Optional.empty() : rating;
        this.ratingDiff = ratingDiff == null ? Optional.empty() : ratingDiff;
        this.provisional = provisional == null ? RatingProvisional.NO : provisional; // Default to NO if null
        this.berserk = berserk;
    }

    public Color getColor() { return color; }
    public Optional<Integer> getAiLevel() { return aiLevel; }
    public Optional<UserId> getUserId() { return userId; }
    public Optional<IntRating> getRating() { return rating; }
    public Optional<IntRatingDiff> getRatingDiff() { return ratingDiff; }
    public RatingProvisional getProvisional() { return provisional; }
    public boolean isBerserk() { return berserk; }
}

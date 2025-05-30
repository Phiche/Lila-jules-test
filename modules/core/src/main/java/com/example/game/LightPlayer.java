package com.example.game;

import com.example.chess.Color;
import com.example.chess.IntRating;
import com.example.chess.IntRatingDiff;
import com.example.chess.RatingProvisional;
import com.example.common.UserId;
import java.util.Optional;

// Basic Placeholder for LightPlayer
public class LightPlayer {
    // Added fields to make it a bit more useful, matching Player.light()
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
        // Constructor to be filled
        this.color = color;
        this.aiLevel = aiLevel;
        this.userId = userId;
        this.rating = rating;
        this.ratingDiff = ratingDiff;
        this.provisional = provisional;
        this.berserk = berserk;
    }

    // Minimal getters for completeness of placeholder
    public Color getColor() { return color; }
    public Optional<Integer> getAiLevel() { return aiLevel; }
    public Optional<UserId> getUserId() { return userId; }
    public Optional<IntRating> getRating() { return rating; }
    public Optional<IntRatingDiff> getRatingDiff() { return ratingDiff; }
    public RatingProvisional getProvisional() { return provisional; }
    public boolean isBerserk() { return berserk; }
}

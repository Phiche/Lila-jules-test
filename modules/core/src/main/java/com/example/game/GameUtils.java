package com.example.game;

import com.example.chess.Centis; // Assuming Centis is available
import com.example.chess.ClockConfig; // Assuming ClockConfig is available
import com.example.chess.Speed; // Assuming Speed is available
import com.example.chess.Variant; // Assuming Variant is available

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class GameUtils {

    private GameUtils() {
        // Private constructor to prevent instantiation
    }

    /**
     * Checks if a game with the given variant and clock configuration can be rated.
     * Ported from Scala:
     *   variant.standard || clock.exists: c =>
     *     c.estimateTotalTime >= Centis(3000) &&
     *       c.limitSeconds > 0 || c.incrementSeconds > 1
     * Note: ClockConfig in Scala had estimateTotalTime, limitSeconds, incrementSeconds.
     * The Java ClockConfig placeholder has getLimit() and getIncrement().
     * We'll need to make assumptions or refine ClockConfig.
     */
    public static boolean allowRated(Variant variant, Optional<ClockConfig> clockConfigOpt) {
        if (variant == Variant.STANDARD) { // Assuming Variant.STANDARD exists
            return true;
        }
        return clockConfigOpt.map(config -> {
            // Placeholder logic for estimateTotalTime, limitSeconds, incrementSeconds
            // Assuming getLimit() is in seconds and getIncrement() is in seconds for this logic.
            // This needs to match how scalachess.Clock.Config works.
            // scalachess ClockConfig limit/increment are in seconds.
            int limitSeconds = config.getLimit();
            int incrementSeconds = config.getIncrement();

            // Estimate total time: limit + 40 * increment (common way in scalachess)
            long estimatedTotalTimeSeconds = limitSeconds + 40L * incrementSeconds;

            boolean hasPositiveLimit = limitSeconds > 0;
            boolean incrementCondition = incrementSeconds > 1; // incrementSeconds > 1 in original

            // Convert estimatedTotalTimeSeconds to centiseconds for comparison with Centis(3000)
            // Centis(3000) = 30 seconds
            return estimatedTotalTimeSeconds >= 30 && (hasPositiveLimit || incrementCondition);
        }).orElse(false); // If no clock config, typically cannot be rated unless it's correspondence (handled by variant usually)
    }

    /**
     * Checks if the clock configuration is suitable for board display/compatibility.
     * Ported from Scala: Speed(clock) >= Speed.Rapid
     */
    public static boolean isBoardCompatible(ClockConfig clockConfig) {
        if (clockConfig == null) return false;
        // This requires Speed to be derivable from ClockConfig and Speed to be Comparable
        // The getSpeed() logic was simplified in Game.java, this might need a shared robust version.
        Speed speed = calculateSpeed(clockConfig); // Using a helper, see below
        return speed.ordinal() >= Speed.RAPID.ordinal(); // Assuming enum order implies >=
    }

    /**
     * Checks if the clock configuration is suitable for bot play.
     * Ported from Scala: Speed(clock) >= Speed.Bullet
     */
    public static boolean isBotCompatible(ClockConfig clockConfig) {
        if (clockConfig == null) return false;
        Speed speed = calculateSpeed(clockConfig);
        return speed.ordinal() >= Speed.BULLET.ordinal();
    }

    /**
     * Helper to calculate speed from ClockConfig. This logic should be centralized
     * if used in multiple places (e.g., in Game.getSpeed() and here).
     * This is a simplified placeholder matching the one in Game.java.
     * Assumes ClockConfig limit/increment are in seconds.
     */
    private static Speed calculateSpeed(ClockConfig config) {
        // This is a placeholder. Real correspondence detection is more complex (e.g., daysPerTurn).
        // If limit and increment are 0, it's ambiguous without more context.
        // For now, this matches the structure of Game.getSpeed() which defaults to CORRESPONDENCE
        // then re-evaluates. A true "clockless" config might be CLASSICAL or some default.
        if (config.getLimit() == 0 && config.getIncrement() == 0) {
             // This should ideally check for daysPerTurn if that info were part of ClockConfig
             // or if this method was on Game. For now, if it's truly 0/0, it's very fast or unlimited.
             // Let's assume it implies a very fast game if not correspondence.
             // However, Game.getSpeed() defaults to CORRESPONDENCE if no clock config.
             // To be consistent with Game.getSpeed() simplified logic for non-correspondence:
             // A 0/0 clock without days is effectively unlimited, often treated as Classical for some contexts,
             // or very fast for others. Let's assume it's not correspondence here.
        }

        // Using the logic from Game.getSpeed() for non-correspondence speeds
        // Assumes config.getLimit() is in seconds.
        int limitInSeconds = config.getLimit();
        // int totalApproximateTime = limitInSeconds + 40 * config.getIncrement(); // More accurate estimate

        if (limitInSeconds < 30) return Speed.ULTRA_BULLET;    // < 0.5 min
        if (limitInSeconds < 180) return Speed.BULLET;       // < 3 min
        if (limitInSeconds < 480) return Speed.BLITZ;        // < 8 min
        if (limitInSeconds < 1500) return Speed.RAPID;       // < 25 min
        return Speed.CLASSICAL;                              // >= 25 min
    }

    /**
     * Interleaves elements from two lists into a single list.
     * Example: a=[1,3,5], b=[2,4,6] -> [1,2,3,4,5,6]
     * If one list is longer, its remaining elements are appended at the end.
     * (This was already implemented in ClockHistory, providing a generic version here)
     */
    public static <A> List<A> interleave(List<A> listA, List<A> listB) {
        List<A> result = new ArrayList<>();
        int i = 0, j = 0;
        while (i < listA.size() || j < listB.size()) {
            if (i < listA.size()) {
                result.add(listA.get(i++));
            }
            if (j < listB.size()) {
                result.add(listB.get(j++));
            }
        }
        return result;
    }

    /**
     * Determines a reasonable minimum number of moves for a game variant.
     * (This was already implemented as private in Game, providing a public static version here)
     */
    public static int getReasonableMinimumNumberOfMoves(Variant variant) {
        if (variant == null) return 15; // Default for unknown variant
        switch (variant) {
            case STANDARD:
            case CHESS960:
            case HORDE:
                return 20;
            case ANTICHESS:
            case CRAZYHOUSE:
            case KINGOFTHEHILL:
                return 15;
            case THREECHECK:
            case ATOMIC:
            case RACINGKINGS:
                return 10;
            // FROM_POSITION is not explicitly listed in scalachess for this method,
            // so it would fall to a default.
            default:
                return 15; // Default for FROM_POSITION or any other unlisted variant
        }
    }
}

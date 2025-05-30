package com.example.common;

import java.time.Instant;
import java.time.ZoneOffset; // For defining GENESIS consistently (though Instant.parse with Z implies UTC)
import java.time.temporal.ChronoUnit;
import java.util.Collections; // For Collections.emptyList()
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public final class LichessDay implements Comparable<LichessDay> {

    private final int value; // Number of days since genesis

    // Define genesis consistently, e.g., at UTC.
    // January 1, 2010, 00:00:00 UTC
    public static final Instant GENESIS_INSTANT = Instant.parse("2010-01-01T00:00:00Z");

    private LichessDay(int value) {
        this.value = value;
    }

    /**
     * Factory method to create a LichessDay from an integer value.
     */
    public static LichessDay ofInt(int value) {
        return new LichessDay(value);
    }

    public int getValue() {
        return value;
    }

    /**
     * Converts this LichessDay back to an Instant, representing the start of that day (UTC).
     */
    public Instant toDate() {
        return GENESIS_INSTANT.plus(value, ChronoUnit.DAYS);
    }

    /**
     * Calculates the LichessDay for a given Instant.
     * The Instant is effectively truncated to the day.
     */
    public static LichessDay dayOf(Instant time) {
        if (time == null) {
            throw new NullPointerException("Time cannot be null for LichessDay.dayOf");
        }
        // ChronoUnit.DAYS.between counts full 24-hour periods.
        // If GENESIS_INSTANT is 2010-01-01T00:00:00Z,
        // then 2010-01-01T10:00:00Z is LichessDay 0. (Days between is 0)
        // and  2010-01-02T05:00:00Z is LichessDay 1. (Days between is 1)
        // This behavior is correct.
        long days = ChronoUnit.DAYS.between(GENESIS_INSTANT, time);
        return new LichessDay((int) days);
    }

    /**
     * Gets the LichessDay for the current moment.
     */
    public static LichessDay today() {
        return dayOf(Instant.now());
    }

    /**
     * Gets the LichessDay for a date a specified number of days ago from now.
     */
    public static LichessDay daysAgo(int days) {
        if (days < 0) { // Or handle as "days in future" if that's meaningful
            // For "days ago", negative input might be ambiguous.
            // Could throw IllegalArgumentException or interpret as 0 days ago.
            // For now, let's assume non-negative.
            // If days is, e.g., -5, it means 5 days in the future.
            // The current minus(-5, unit) would correctly add 5 days.
            // So, this is fine.
        }
        return dayOf(Instant.now().minus(days, ChronoUnit.DAYS));
    }

    /**
     * Gets a list of the last nb LichessDays, including today.
     * The list is ordered from most recent (today) to oldest.
     */
    public static List<LichessDay> recent(int nb) {
        if (nb <= 0) { // Changed from <0 to <=0 to handle nb=0 explicitly
            return Collections.emptyList();
        }
        Instant now = Instant.now();
        return IntStream.range(0, nb) // Generates 0, 1, ..., nb-1
                .mapToObj(i -> dayOf(now.minus(i, ChronoUnit.DAYS)))
                .collect(Collectors.toList());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LichessDay that = (LichessDay) o;
        return value == that.value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return "LichessDay(" + value + ")";
    }

    @Override
    public int compareTo(LichessDay other) {
        return Integer.compare(this.value, other.value);
    }
}

package com.example.game;

import com.example.chess.Centis;
import com.example.chess.Clock;
import com.example.chess.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;


public class ClockHistory {
    private final List<Centis> white;
    private final List<Centis> black;

    public static final Optional<ClockHistory> SOME_EMPTY = Optional.of(new ClockHistory(Collections.emptyList(), Collections.emptyList()));

    public ClockHistory(List<Centis> white, List<Centis> black) {
        this.white = white == null ? Collections.emptyList() : Collections.unmodifiableList(new ArrayList<>(white));
        this.black = black == null ? Collections.emptyList() : Collections.unmodifiableList(new ArrayList<>(black));
    }

    public List<Centis> getWhite() { return white; }
    public List<Centis> getBlack() { return black; }

    public ClockHistory update(Color color, Function<List<Centis>, List<Centis>> f) {
        if (color == Color.WHITE) {
            return new ClockHistory(f.apply(new ArrayList<>(white)), black);
        } else {
            return new ClockHistory(white, f.apply(new ArrayList<>(black)));
        }
    }

    public ClockHistory record(Color color, Clock clock) {
        // Assuming clock.getRemainingTime(color) returns Centis
        // The current placeholder Clock has clock.getTime() but it's not per-color.
        // For now, using clock.getTime() as a generic placeholder.
        // A more accurate implementation would be:
        // Centis remainingTime = clock.getPlayers().get(color).remainingTime(); // If PlayerClock had remainingTime
        // Or if Clock itself provides remaining time for a color.
        Centis remainingTime = new Centis(clock.getTime()); // Placeholder: clock.getRemainingTime(color) or similar

        Function<List<Centis>, List<Centis>> recordFn = list -> {
            List<Centis> newList = new ArrayList<>(list);
            newList.add(remainingTime);
            return newList;
        };
        return update(color, recordFn);
    }

    public ClockHistory reset(Color color) {
        return update(color, list -> Collections.emptyList());
    }

    public List<Centis> getForColor(Color color) { // Renamed from apply to avoid confusion in Java
        return (color == Color.WHITE) ? white : black;
    }

    public Optional<Centis> getLast(Color color) {
        List<Centis> times = getForColor(color);
        return times.isEmpty() ? Optional.empty() : Optional.of(times.get(times.size() - 1));
    }

    public int getSize() {
        return white.size() + black.size();
    }

    // first state is of the color that moved first.
    public List<Centis> getBothClockStates(Color firstMoveBy) {
        List<Centis> first = (firstMoveBy == Color.WHITE) ? white : black;
        List<Centis> second = (firstMoveBy == Color.WHITE) ? black : white;

        List<Centis> interleaved = new ArrayList<>();
        int i = 0, j = 0;
        while (i < first.size() || j < second.size()) {
            if (i < first.size()) {
                interleaved.add(first.get(i++));
            }
            if (j < second.size()) {
                interleaved.add(second.get(j++));
            }
        }
        return Collections.unmodifiableList(interleaved);
    }
}

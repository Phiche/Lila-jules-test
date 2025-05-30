package com.example.game;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional; // Added for byId
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public enum Source {
    LOBBY(1),
    FRIEND(2),
    AI(3),
    API(4),
    ARENA(5),
    POSITION(6),
    IMPORT(7),
    IMPORT_LIVE(9), // Original comment: wut?
    SIMUL(10),
    POOL(12),
    SWISS(13);

    private final int id;

    Source(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return this.name().toLowerCase();
    }

    public static final Map<Integer, Source> BY_ID =
            Arrays.stream(values()).collect(Collectors.toMap(Source::getId, Function.identity()));

    public static final Map<String, Source> BY_NAME =
            Arrays.stream(values()).collect(Collectors.toMap(Source::getName, Function.identity()));

    public static final List<Source> SEARCHABLE = Collections.unmodifiableList(Arrays.asList(
            LOBBY, FRIEND, AI, POSITION, ARENA, SIMUL, POOL, SWISS
    ));

    public static final Set<Source> EXPIRABLE = Collections.unmodifiableSet(
            Arrays.stream(new Source[]{LOBBY, ARENA, POOL, SWISS}).collect(Collectors.toSet())
    );

    public static Optional<Source> byId(int id) { // Added Optional import
        return Optional.ofNullable(BY_ID.get(id));
    }
}

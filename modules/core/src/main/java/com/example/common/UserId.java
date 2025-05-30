package com.example.common;

import java.util.Arrays;
import java.util.List;

public class UserId {
    private final String id;

    public static final UserId LICHESS = new UserId("lichess");
    public static final UserId GHOST = new UserId("ghost");
    public static final UserId EXPLORER = new UserId("openingexplorer");
    public static final UserId BROADCASTER = new UserId("broadcaster");
    public static final UserId IRWIN = new UserId("irwin");
    public static final UserId KALADIN = new UserId("kaladin");
    public static final UserId LICHESS4545 = new UserId("lichess4545");
    public static final UserId CHALLENGERMODE = new UserId("challengermode");
    public static final UserId WATCHERBOT = new UserId("watcherbot");

    private static final List<UserId> OFFICIAL_USERS = Arrays.asList(LICHESS, BROADCASTER);

    public UserId(String id) {
        this.id = id.toLowerCase();
    }

    public String getId() {
        return id;
    }

    public boolean isGhost() {
        return id.equals(GHOST.getId()) || id.startsWith("!");
    }

    public boolean noGhost() {
        return !isGhost();
    }

    public static boolean isOfficial(UserId userId) {
        return OFFICIAL_USERS.contains(userId);
    }
}

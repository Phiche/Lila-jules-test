package com.example.game;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public enum GameRule {
    NO_ABORT("noAbort"),
    NO_REMATCH("noRematch"),
    NO_GIVE_TIME("noGiveTime"),
    NO_CLAIM_WIN("noClaimWin"),
    NO_EARLY_DRAW("noEarlyDraw");

    private final String key;

    GameRule(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    public static final Map<String, GameRule> BY_KEY =
            Arrays.stream(values()).collect(Collectors.toMap(GameRule::getKey, Function.identity()));
}

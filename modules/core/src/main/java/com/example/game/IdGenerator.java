package com.example.game;

import com.example.common.GameId;
import java.util.List;
import java.util.UUID; // Using UUID for a simple unchecked ID for now
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.apache.commons.lang3.RandomStringUtils; // For random string generation


public interface IdGenerator {

    CompletableFuture<GameId> generateGameId();

    CompletableFuture<List<GameId>> generateGameIds(int nb);

    // CompletableFuture<Game> withUniqueId(NewGame newGame); // NewGame will be created later
    // Temporarily commenting out until NewGame is defined
    // To make this file compilable standalone for now.

    /**
     * Generates an unchecked (potentially non-unique) GameId.
     * Scala version used ThreadLocalRandom.nextString(GameId.size).
     * GameId.size is 8 in Scala.
     */
    static GameId uncheckedGame() {
        // A simple UUID based or random string based generator can be used here.
        // Using Apache Commons Lang for RandomStringUtils, ensure it's in pom.xml if chosen
        int gameIdSize = 8; // As defined in lila.core.id.GameId in Scala
        String randomId = RandomStringUtils.randomAlphanumeric(gameIdSize).toLowerCase(); // Match lila's lowercase IDs
        return new GameId(randomId);
    }
}

package com.example.game;

import com.example.common.SimulId;
import com.example.common.SwissId;
import com.example.common.TourId;
import com.example.common.UserId; // For PgnUser getter
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class GameMetadata {
    private final Optional<Source> source;
    private final Optional<PgnImport> pgnImport;
    private final Optional<TourId> tournamentId;
    private final Optional<SwissId> swissId;
    private final Optional<SimulId> simulId;
    private final boolean analysed;
    private final GameDrawOffers drawOffers;
    private final Set<GameRule> rules;

    public static final GameMetadata EMPTY = new GameMetadata(
            Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
            false, GameDrawOffers.EMPTY, Collections.emptySet());

    public GameMetadata(Optional<Source> source, Optional<PgnImport> pgnImport,
                        Optional<TourId> tournamentId, Optional<SwissId> swissId,
                        Optional<SimulId> simulId, boolean analysed,
                        GameDrawOffers drawOffers, Set<GameRule> rules) {
        this.source = source;
        this.pgnImport = pgnImport;
        this.tournamentId = tournamentId;
        this.swissId = swissId;
        this.simulId = simulId;
        this.analysed = analysed;
        this.drawOffers = drawOffers == null ? GameDrawOffers.EMPTY : drawOffers;
        this.rules = rules == null ? Collections.emptySet() : Collections.unmodifiableSet(new HashSet<>(rules));
    }

    public Optional<Source> getSource() { return source; }
    public Optional<PgnImport> getPgnImport() { return pgnImport; }
    public Optional<TourId> getTournamentId() { return tournamentId; }
    public Optional<SwissId> getSwissId() { return swissId; }
    public Optional<SimulId> getSimulId() { return simulId; }
    public boolean isAnalysed() { return analysed; }
    public GameDrawOffers getDrawOffers() { return drawOffers; }
    public Set<GameRule> getRules() { return rules; }

    public Optional<String> getPgnDate() {
        return pgnImport.flatMap(PgnImport::getDate);
    }

    public Optional<UserId> getPgnUser() { // Corrected return type to Optional<UserId>
        return pgnImport.flatMap(PgnImport::getUser);
    }

    public boolean hasRule(GameRule rule) { // Simplified from Scala version
        return rules.contains(rule);
    }

    public Optional<Set<GameRule>> getNonEmptyRules() {
        return rules.isEmpty() ? Optional.empty() : Optional.of(rules);
    }
    
    public static GameMetadata newMetadata(Source source) {
        return new GameMetadata(Optional.ofNullable(source), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
            false, GameDrawOffers.EMPTY, Collections.emptySet());
    }
}

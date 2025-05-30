package com.example.i18n;

import com.example.common.scalatags.RawFrag; // Placeholder
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class I18nKey {
    private final String key;

    public I18nKey(String key) {
        Objects.requireNonNull(key, "I18nKey key cannot be null");
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    // Methods from Scala extension block
    public String txt(Translate translate, Object... args) {
        Objects.requireNonNull(translate, "Translate object cannot be null for txt()");
        return translate.getTranslator().getTxt().literal(this, Arrays.asList(args), translate.getLang());
    }

    public String pluralTxt(Translate translate, long count, Object... args) {
        Objects.requireNonNull(translate, "Translate object cannot be null for pluralTxt()");
        return translate.getTranslator().getTxt().plural(this, count, Arrays.asList(args), translate.getLang());
    }

    public String pluralSameTxt(Translate translate, long count) {
        // In Scala, the 'count' argument was passed again as the first element of 'args'
        // Replicating that behavior if necessary, or just passing count if the translator handles it.
        // For now, assuming the translator's plural method might expect 'count' as one of the args if not separately handled.
        // This might need adjustment based on how TranslatorTxt.plural is implemented.
        return pluralTxt(translate, count, count); // Passing count as the only arg for "same"
    }

    // Renamed from apply to frag to avoid constructor confusion
    public RawFrag frag(Translate translate, Object... args) {
        Objects.requireNonNull(translate, "Translate object cannot be null for frag()");
        return translate.getTranslator().getFrag().literal(this, Arrays.asList(args), translate.getLang());
    }

    public RawFrag plural(Translate translate, long count, Object... args) {
        Objects.requireNonNull(translate, "Translate object cannot be null for plural()");
        return translate.getTranslator().getFrag().plural(this, count, Arrays.asList(args), translate.getLang());
    }

    public RawFrag pluralSame(Translate translate, long count) { // Changed int count to long to match others
         // Similar to pluralSameTxt, passing count as the only arg.
        return plural(translate, count, count);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        I18nKey i18nKey = (I18nKey) o;
        return key.equals(i18nKey.key);
    }

    @Override
    public int hashCode() {
        return Objects.hash(key);
    }

    @Override
    public String toString() {
        return key;
    }

    // --- Nested classes for key groups ---

    public static final class activity {
        private activity() {} // Prevent instantiation
        public static final I18nKey activity = new I18nKey("activity:activity");
        public static final I18nKey hostedALiveStream = new I18nKey("activity:hostedALiveStream");
        public static final I18nKey rankedInSwissTournament = new I18nKey("activity:rankedInSwissTournament");
        public static final I18nKey signedUp = new I18nKey("activity:signedUp");
        public static final I18nKey supportedNbMonths = new I18nKey("activity:supportedNbMonths");
        public static final I18nKey practicedNbPositions = new I18nKey("activity:practicedNbPositions");
        public static final I18nKey solvedNbPuzzles = new I18nKey("activity:solvedNbPuzzles");
        public static final I18nKey playedNbGames = new I18nKey("activity:playedNbGames");
        public static final I18nKey postedNbMessages = new I18nKey("activity:postedNbMessages");
        public static final I18nKey playedNbMoves = new I18nKey("activity:playedNbMoves");
        public static final I18nKey inNbCorrespondenceGames = new I18nKey("activity:inNbCorrespondenceGames");
        public static final I18nKey completedNbGames = new I18nKey("activity:completedNbGames");
        public static final I18nKey completedNbVariantGames = new I18nKey("activity:completedNbVariantGames");
        public static final I18nKey followedNbPlayers = new I18nKey("activity:followedNbPlayers");
        public static final I18nKey gainedNbFollowers = new I18nKey("activity:gainedNbFollowers");
        public static final I18nKey hostedNbSimuls = new I18nKey("activity:hostedNbSimuls");
        public static final I18nKey joinedNbSimuls = new I18nKey("activity:joinedNbSimuls");
        public static final I18nKey createdNbStudies = new I18nKey("activity:createdNbStudies");
        public static final I18nKey competedInNbTournaments = new I18nKey("activity:competedInNbTournaments");
        public static final I18nKey rankedInTournament = new I18nKey("activity:rankedInTournament");
        public static final I18nKey competedInNbSwissTournaments = new I18nKey("activity:competedInNbSwissTournaments");
        public static final I18nKey joinedNbTeams = new I18nKey("activity:joinedNbTeams");
    }

    public static final class appeal {
        private appeal() {}
        public static final I18nKey cleanAllGood = new I18nKey("appeal:cleanAllGood");
        public static final I18nKey engineMarked = new I18nKey("appeal:engineMarked");
        public static final I18nKey engineMarkedInfo = new I18nKey("appeal:engineMarkedInfo");
        public static final I18nKey arenaBanned = new I18nKey("appeal:arenaBanned");
        public static final I18nKey prizeBanned = new I18nKey("appeal:prizeBanned");
        // ... (add all other keys from appeal section)
    }

    // Placeholder for a 'site' section often used by translateDuration
    public static final class site {
        private site() {}
        public static final I18nKey nbDays = new I18nKey("nbDays"); // Assuming this is how it was referenced
        public static final I18nKey nbHours = new I18nKey("nbHours");
        public static final I18nKey nbMinutes = new I18nKey("nbMinutes");
    }

    // ... (Other nested classes like arena, broadcast, etc. will be added progressively)
}

package com.example.i18n;

public enum I18nModule {
    SITE,
    ARENA,
    EMAILS,
    LEARN,
    ACTIVITY,
    COORDINATES,
    STUDY,
    CLASS_MODULE, // Renamed from 'class'
    CONTACT,
    APPEAL,
    PATRON,
    COACH,
    BROADCAST,
    STREAMER,
    TFA,
    SETTINGS,
    PREFERENCES,
    TEAM,
    PERF_STAT,
    SEARCH,
    TOURNAME,
    FAQ,
    LAG,
    SWISS,
    PUZZLE,
    PUZZLE_THEME,
    CHALLENGE,
    STORM,
    UBLOG,
    INSIGHT,
    KEYBOARD_MOVE,
    TIMEAGO,
    OAUTH_SCOPE,
    DGT,
    VOICE_COMMANDS,
    ONBOARDING,
    FEATURES,
    NVUI;

    // If the exact string value of the Scala enum members is needed (e.g., "class", "perfStat")
    // then a field and constructor like the GameRule enum would be needed.
    // For now, using standard Java enum naming conventions.
    // Example:
    // private final String originalName;
    // I18nModule(String originalName) { this.originalName = originalName; }
    // I18nModule() { this.originalName = this.name().toLowerCase(); } // Default if not clashing
    // public String getOriginalName() { return originalName; }
}

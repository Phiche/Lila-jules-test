package com.example.db; // Placing in db package

public final class BSONFields {
    private BSONFields() {} // Private constructor to prevent instantiation

    public static final String ID = "_id";
    public static final String PLAYER_UIDS = "us";
    public static final String WINNER_ID = "wid";
    public static final String CREATED_AT = "ca";
    public static final String MOVED_AT = "ua"; // ua = updatedAt (bc)
    public static final String TURNS = "t";
    public static final String ANALYSED = "an";
    public static final String PGN_IMPORT = "pgni";
    public static final String PLAYING_UIDS = "pl";
}

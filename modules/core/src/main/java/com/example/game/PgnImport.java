package com.example.game;

import com.example.common.PgnStr;
import com.example.common.UserId;
import java.util.Optional;

public class PgnImport {
    private final Optional<UserId> user;
    private final Optional<String> date;
    private final PgnStr pgn;
    private final Optional<byte[]> h; // Hashed PGN

    public PgnImport(Optional<UserId> user, Optional<String> date, PgnStr pgn, Optional<byte[]> h) {
        this.user = user;
        this.date = date;
        this.pgn = pgn;
        this.h = h;
    }

    public Optional<UserId> getUser() { return user; }
    public Optional<String> getDate() { return date; }
    public PgnStr getPgn() { return pgn; }
    public Optional<byte[]> getH() { return h; }
}

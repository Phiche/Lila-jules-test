package com.example.lightuser;

import com.example.common.PlayerTitle;
import com.example.common.Flair;
import com.example.common.UserId;
import com.example.common.UserName;

import java.util.Optional;

public class LightUser {

    private final UserId id;
    private final UserName name;
    private final Optional<PlayerTitle> title;
    private final Optional<Flair> flair;
    private final boolean isPatron;

    public static final LightUser GHOST = new LightUser(
            new UserId("ghost"),
            new UserName("ghost"),
            Optional.empty(),
            Optional.empty(),
            false
    );

    public LightUser(UserId id, UserName name, Optional<PlayerTitle> title, Optional<Flair> flair, boolean isPatron) {
        this.id = id;
        this.name = name;
        this.title = title;
        this.flair = flair;
        this.isPatron = isPatron;
    }

    public UserId getId() {
        return id;
    }

    public UserName getName() {
        return name;
    }

    public Optional<PlayerTitle> getTitle() {
        return title;
    }

    public Optional<Flair> getFlair() {
        return flair;
    }

    public boolean isPatron() {
        return isPatron;
    }

    public String getTitleName() {
        return title.map(t -> t.getValue() + " " + name.getValue()).orElse(name.getValue());
    }

    public boolean isBot() {
        return title.map(PlayerTitle::isBot).orElse(false);
    }

    public static LightUser fallback(UserName name) {
        return new LightUser(
                name.getId(),
                name,
                Optional.empty(),
                Optional.empty(),
                false
        );
    }
}

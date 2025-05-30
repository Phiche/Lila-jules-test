package com.example.captcha;

import com.example.common.GameId;

public interface WithCaptcha {
    GameId getGameId();
    String getMove();
}

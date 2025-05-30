package com.example.captcha;

import com.example.common.GameId;
import java.util.concurrent.CompletableFuture;

public interface CaptchaApi {
    Captcha any();
    CompletableFuture<Captcha> get(GameId id);
    CompletableFuture<Boolean> validate(GameId gameId, String move);
    boolean validateSync(WithCaptcha data);
}

package com.example.lilaism;

import com.typesafe.config.Config;
import java.time.Duration;
import java.util.concurrent.TimeUnit;

public class ConfigUtils {

    public static int millis(Config self, String name) {
        return (int) self.getDuration(name, TimeUnit.MILLISECONDS);
    }

    public static int seconds(Config self, String name) {
        return (int) self.getDuration(name, TimeUnit.SECONDS);
    }

    public static Duration duration(Config self, String name) {
        return Duration.ofMillis(millis(self, name));
    }
}

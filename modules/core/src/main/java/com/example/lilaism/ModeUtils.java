package com.example.lilaism;

import com.example.common.PlayMode;

public class ModeUtils {

    public static boolean isDev(PlayMode mode) {
        return mode == PlayMode.DEV;
    }

    public static boolean isProd(PlayMode mode) {
        return mode == PlayMode.PROD;
    }

    public static boolean notProd(PlayMode mode) {
        return mode != PlayMode.PROD;
    }
}

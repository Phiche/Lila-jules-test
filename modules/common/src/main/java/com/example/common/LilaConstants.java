package com.example.common; // Target package for common module constants

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public final class LilaConstants {

    private LilaConstants() {
        // Private constructor to prevent instantiation
    }

    public static final List<String> BANNED_YOUTUBE_IDS = Collections.unmodifiableList(Arrays.asList(
            "7UpltimWY_E",
            "J_bzfjZZnjU",
            "xRiQe_tq7h0",
            "8IlVvluRbwk",
            "P3o0hPjrxgo",
            "d8TSD4f89i8",
            "b-WYer6Mjh0",
            "0GfVOMBIuLo",
            "g-DKTxYFkRQ",
            "Z5eWjccV7D0",
            "i3BT785qTWw",
            "K_Bn2phvLro",
            "V11ZQyAhEAM",
            "i3BT785qTWw", // Note: This ID is duplicated in the original list
            "ogavmoKE04Y",
            "JPim0DiXNHk",
            "roMv8RjhAQU",
            "8JdXqxGwtJU"
    ));

    // Other constants from the 'common' module can be added here later.
}

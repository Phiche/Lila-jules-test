package com.example.lilaism;

import java.time.Duration;
// Assuming chess.Centis will be defined or is a simple wrapper.
// import com.example.chess.Centis; 

public class FiniteDurationUtils {

    // public static Centis toCentis(Duration self) {
    //     // Conversion logic from Duration to Centis
    //     // return new Centis(self.toMillis() / 10); // Example, if Centis is 1/100th of a second
    // }

    public static Duration abs(Duration self) {
        return self.isNegative() ? self.negated() : self;
    }
}

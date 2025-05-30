package com.example.lilaism;

import java.util.Optional;
import java.util.function.Supplier; // For lazy evaluation of error messages

public class OptionUtils {

    public static <A> A err(Optional<A> self, Supplier<String> messageSupplier) {
        return self.orElseThrow(() -> new RuntimeException(messageSupplier.get()));
    }
}

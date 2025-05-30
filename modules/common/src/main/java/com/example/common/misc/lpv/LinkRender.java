package com.example.common.misc.lpv;

import com.example.common.scalatags.RawFrag; // Assuming RawFrag from com.example.common.scalatags
import java.util.Optional;

// Placeholder for lila.core.misc.lpv.LinkRender
// This was originally (String, String) => Option[Frag]
// Representing it as a functional interface.
@FunctionalInterface
public interface LinkRender {
    Optional<RawFrag> render(String path, String text);
}

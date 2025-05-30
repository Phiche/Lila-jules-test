package com.example.game;

import com.example.chess.Color;
import com.example.chess.IntRating;
import com.example.chess.RatingProvisional;
import com.example.common.UserId;
// Import Perf and WithPerf if/when they are defined
// import com.example.perf.Perf;
// import com.example.user.WithPerf;
// import com.example.common.Pair; // Assuming a common Pair class might exist later

import java.util.Optional;

public interface NewPlayerFactory {
    // Player create(Color color, Optional<WithPerf> user); // Requires WithPerf
    
    Player create(Color color, UserId userId, IntRating rating, RatingProvisional provisional);
    
    // Player create(Color color, Pair<UserId, Perf> userPerf); // Requires Pair and Perf
    
    Player createAnon(Color color, Optional<Integer> aiLevel);
}

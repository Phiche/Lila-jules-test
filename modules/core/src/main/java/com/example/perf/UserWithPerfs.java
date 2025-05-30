package com.example.perf;

import com.example.user.User; // Assuming User placeholder exists
import java.util.Collections;
import java.util.Map; // Example for perfs

// Placeholder for lila.rating.UserWithPerfs / lila.perf.UserWithPerfs
public class UserWithPerfs {
    private final User user;
    private final Map<PerfKey, Perf> perfs; // Assuming PerfKey and Perf placeholders/classes

    public UserWithPerfs(User user, Map<PerfKey, Perf> perfs) {
        this.user = user;
        this.perfs = perfs == null ? Collections.emptyMap() : Collections.unmodifiableMap(perfs);
    }

    public User getUser() { return user; }
    public Map<PerfKey, Perf> getPerfs() { return perfs; }
}

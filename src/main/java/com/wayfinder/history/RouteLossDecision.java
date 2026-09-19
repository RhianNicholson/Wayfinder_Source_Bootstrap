package com.wayfinder.history;

import java.util.Optional;

public record RouteLossDecision(
        boolean occurs,
        Optional<RouteLossCandidate> candidate,
        double roll,
        double threshold
) {
    public RouteLossDecision {
        candidate = candidate == null ? Optional.empty() : candidate;
    }

    public static RouteLossDecision none() {
        return new RouteLossDecision(false, Optional.empty(), 1.0, 0.0);
    }
}

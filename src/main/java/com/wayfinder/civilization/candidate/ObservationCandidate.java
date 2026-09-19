package com.wayfinder.civilization.candidate;

import com.wayfinder.core.math.WorldPosition;
import com.wayfinder.geography.model.Landmark;
import com.wayfinder.geography.model.ObservationSite;
import com.wayfinder.geography.visibility.SightlineResult;

public record ObservationCandidate(
        String candidateKey,
        WorldPosition position,
        Landmark target,
        ObservationSite site,
        SightlineResult sightline
) {
    public ObservationCandidate {
        if (candidateKey == null || candidateKey.isBlank()) {
            throw new IllegalArgumentException("candidateKey is required");
        }
    }
}

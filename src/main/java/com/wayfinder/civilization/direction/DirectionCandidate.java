package com.wayfinder.civilization.direction;

import com.wayfinder.civilization.state.CivilizationNode;
import com.wayfinder.core.math.WorldPosition;

/**
 * A geographically possible place for a DIRECTION node whose destination is
 * an already committed OBSERVATION node.
 *
 * This is still a possibility, not permission to exist.
 */
public record DirectionCandidate(
        String candidateKey,
        WorldPosition position,
        CivilizationNode destination,
        double distance,
        double buildability,
        double travelFit,
        double directionalClarity
) {
    public DirectionCandidate {
        if (candidateKey == null || candidateKey.isBlank()) {
            throw new IllegalArgumentException("candidateKey is required");
        }
        if (position == null) throw new IllegalArgumentException("position is required");
        if (destination == null) throw new IllegalArgumentException("destination is required");
    }
}

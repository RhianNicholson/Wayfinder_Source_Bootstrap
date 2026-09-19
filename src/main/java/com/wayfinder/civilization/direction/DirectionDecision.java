package com.wayfinder.civilization.direction;

import com.wayfinder.civilization.model.NodePurpose;

public record DirectionDecision(
        NodePurpose purpose,
        ScoredDirectionCandidate selected,
        int generatedCandidates,
        int validCandidates,
        int plausibleCandidates
) {
    public DirectionDecision {
        if (purpose != NodePurpose.DIRECTION) {
            throw new IllegalArgumentException(
                    "DirectionDecision purpose must be DIRECTION"
            );
        }
    }
}

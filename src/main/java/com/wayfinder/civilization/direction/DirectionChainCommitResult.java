package com.wayfinder.civilization.direction;

import com.wayfinder.civilization.state.CivilizationState;

public record DirectionChainCommitResult(
        boolean committed,
        CivilizationState state,
        String stage
) {
    public DirectionChainCommitResult {
        if (state == null) {
            throw new IllegalArgumentException(
                    "state is required"
            );
        }

        if (stage == null || stage.isBlank()) {
            throw new IllegalArgumentException(
                    "stage is required"
            );
        }
    }
}

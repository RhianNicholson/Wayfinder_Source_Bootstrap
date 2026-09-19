package com.wayfinder.civilization.commit;

import com.wayfinder.civilization.state.CivilizationState;

public record CommitResult(
        boolean committed,
        CivilizationState state,
        TransitionValidationResult validation
) {}

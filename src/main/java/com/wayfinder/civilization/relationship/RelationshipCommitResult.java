package com.wayfinder.civilization.relationship;

import com.wayfinder.civilization.state.CivilizationState;

public record RelationshipCommitResult(
        boolean committed,
        CivilizationState state,
        RelationshipTransitionValidationResult validation
) {}

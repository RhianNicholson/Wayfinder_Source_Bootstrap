package com.wayfinder.civilization.relationship;

import com.wayfinder.civilization.state.CivilizationState;

import java.util.ArrayList;

public final class RelationshipCommitService {
    private final RelationshipTransitionValidator validator;

    public RelationshipCommitService(
            RelationshipTransitionValidator validator
    ) {
        this.validator = validator;
    }

    public RelationshipCommitResult commit(
            RelationshipTransition transition,
            CivilizationState current
    ) {
        RelationshipTransitionValidationResult validation =
                validator.validate(
                        transition,
                        current
                );

        if (!validation.accepted()) {
            return new RelationshipCommitResult(
                    false,
                    current,
                    validation
            );
        }

        var nextRelationships =
                new ArrayList<CivilizationRelationship>(
                        current.relationships()
                );

        nextRelationships.add(
                transition.relationship()
        );

        CivilizationState next =
                new CivilizationState(
                        current.nodes(),
                        nextRelationships
                );

        return new RelationshipCommitResult(
                true,
                next,
                validation
        );
    }
}

package com.wayfinder.civilization.relationship;

import com.wayfinder.civilization.state.CivilizationState;

import java.util.ArrayList;
import java.util.List;

public final class RelationshipTransitionValidator {

    public RelationshipTransitionValidationResult validate(
            RelationshipTransition transition,
            CivilizationState state
    ) {
        List<RelationshipTransitionValidationIssue> issues =
                new ArrayList<>();

        if (transition == null
                || transition.proposal() == null
                || transition.relationship() == null) {

            issues.add(
                    new RelationshipTransitionValidationIssue(
                            RelationshipTransitionValidationCode.INVALID_TRANSITION,
                            "Relationship transition is incomplete."
                    )
            );

            return RelationshipTransitionValidationResult.reject(
                    issues
            );
        }

        CivilizationRelationship relationship =
                transition.relationship();

        boolean sourceExists = state.nodes().stream()
                .anyMatch(node ->
                        node.id().equals(
                                relationship.sourceNodeId()
                        )
                );

        if (!sourceExists) {
            issues.add(
                    new RelationshipTransitionValidationIssue(
                            RelationshipTransitionValidationCode.SOURCE_NODE_MISSING,
                            "Source node is no longer present in committed civilization state."
                    )
            );
        }

        boolean targetExists = state.nodes().stream()
                .anyMatch(node ->
                        node.id().equals(
                                relationship.targetNodeId()
                        )
                );

        if (!targetExists) {
            issues.add(
                    new RelationshipTransitionValidationIssue(
                            RelationshipTransitionValidationCode.TARGET_NODE_MISSING,
                            "Target node is no longer present in committed civilization state."
                    )
            );
        }

        boolean duplicateId = state.relationships().stream()
                .anyMatch(existing ->
                        existing.id().equals(
                                relationship.id()
                        )
                );

        if (duplicateId) {
            issues.add(
                    new RelationshipTransitionValidationIssue(
                            RelationshipTransitionValidationCode.DUPLICATE_RELATIONSHIP_ID,
                            "A committed relationship already has this deterministic id."
                    )
            );
        }

        boolean duplicateProposal =
                state.relationships().stream()
                        .anyMatch(existing ->
                                existing.sourceProposalKey()
                                        .equals(
                                                relationship.sourceProposalKey()
                                        )
                        );

        if (duplicateProposal) {
            issues.add(
                    new RelationshipTransitionValidationIssue(
                            RelationshipTransitionValidationCode.DUPLICATE_SOURCE_PROPOSAL,
                            "This relationship proposal has already been committed."
                    )
            );
        }

        boolean duplicateSemanticEdge =
                state.relationships().stream()
                        .anyMatch(existing ->
                                existing.type()
                                        == relationship.type()
                                        && existing.sourceNodeId()
                                                .equals(
                                                        relationship.sourceNodeId()
                                                )
                                        && existing.targetNodeId()
                                                .equals(
                                                        relationship.targetNodeId()
                                                )
                        );

        if (duplicateSemanticEdge) {
            issues.add(
                    new RelationshipTransitionValidationIssue(
                            RelationshipTransitionValidationCode.DUPLICATE_SEMANTIC_EDGE,
                            "An equivalent semantic relationship already exists."
                    )
            );
        }

        return issues.isEmpty()
                ? RelationshipTransitionValidationResult.allow()
                : RelationshipTransitionValidationResult.reject(
                        issues
                );
    }
}

package com.wayfinder.civilization.commit;

import com.wayfinder.civilization.state.CivilizationState;

import java.util.ArrayList;
import java.util.List;

public final class NodeTransitionValidator {

    public TransitionValidationResult validate(
            NodeTransition transition,
            CivilizationState state
    ) {
        List<TransitionValidationIssue> issues = new ArrayList<>();

        if (transition == null || transition.node() == null || transition.proposal() == null) {
            issues.add(new TransitionValidationIssue(
                    TransitionValidationCode.INVALID_TRANSITION,
                    "Transition is incomplete."
            ));
            return TransitionValidationResult.reject(issues);
        }

        boolean duplicateId = state.nodes().stream()
                .anyMatch(existing -> existing.id().equals(transition.node().id()));

        if (duplicateId) {
            issues.add(new TransitionValidationIssue(
                    TransitionValidationCode.DUPLICATE_NODE_ID,
                    "A committed node already has this deterministic node id."
            ));
        }

        boolean duplicateProposal = state.nodes().stream()
                .anyMatch(existing ->
                        existing.sourceProposalKey().equals(
                                transition.node().sourceProposalKey()
                        )
                );

        if (duplicateProposal) {
            issues.add(new TransitionValidationIssue(
                    TransitionValidationCode.DUPLICATE_SOURCE_PROPOSAL,
                    "This proposal has already been committed."
            ));
        }

        return issues.isEmpty()
                ? TransitionValidationResult.allow()
                : TransitionValidationResult.reject(issues);
    }
}

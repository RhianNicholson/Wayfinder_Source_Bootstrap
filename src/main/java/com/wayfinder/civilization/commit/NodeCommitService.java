package com.wayfinder.civilization.commit;

import com.wayfinder.civilization.state.CivilizationNode;
import com.wayfinder.civilization.state.CivilizationState;

import java.util.ArrayList;

public final class NodeCommitService {
    private final NodeTransitionValidator validator;

    public NodeCommitService(NodeTransitionValidator validator) {
        this.validator = validator;
    }

    public CommitResult commit(
            NodeTransition transition,
            CivilizationState current
    ) {
        TransitionValidationResult validation =
                validator.validate(transition, current);

        if (!validation.accepted()) {
            return new CommitResult(
                    false,
                    current,
                    validation
            );
        }

        var nextNodes =
                new ArrayList<CivilizationNode>(
                        current.nodes()
                );

        nextNodes.add(transition.node());

        /*
         * Nodes and relationships are independent parts of the same
         * authoritative civilization state. Adding a node must never erase
         * already committed historical edges.
         */
        CivilizationState next =
                new CivilizationState(
                        nextNodes,
                        current.relationships()
                );

        return new CommitResult(
                true,
                next,
                validation
        );
    }
}

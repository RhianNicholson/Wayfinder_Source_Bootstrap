package com.wayfinder.civilization.relationship;

import com.wayfinder.civilization.direction.DirectionDecision;
import com.wayfinder.civilization.state.CivilizationNode;

/**
 * Converts the reasoned Direction decision into the first explicit network
 * relationship:
 *
 * DIRECTION -> OBSERVATION
 */
public final class DirectionalReferenceProposalFactory {

    public RelationshipProposal create(
            CivilizationNode directionNode,
            DirectionDecision decision
    ) {
        var selected = decision.selected();
        var destination =
                selected.candidate()
                        .destination();

        return new RelationshipProposal(
                "DREF:"
                        + directionNode.id().value()
                        + "->"
                        + destination.id().value(),
                RelationshipType.DIRECTIONAL_REFERENCE,
                directionNode.id(),
                destination.id(),
                selected.score().finalScore(),
                selected.reasons()
        );
    }
}

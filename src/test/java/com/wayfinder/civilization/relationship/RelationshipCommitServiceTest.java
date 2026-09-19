package com.wayfinder.civilization.relationship;

import com.wayfinder.civilization.model.NodePurpose;
import com.wayfinder.civilization.reason.ReasonEvaluation;
import com.wayfinder.civilization.reason.ReasonRecord;
import com.wayfinder.civilization.reason.ReasonType;
import com.wayfinder.civilization.state.CivilizationNode;
import com.wayfinder.civilization.state.CivilizationState;
import com.wayfinder.civilization.target.CivilizationNodeTarget;
import com.wayfinder.civilization.target.GeographicNodeTarget;
import com.wayfinder.core.id.GeographicFeatureId;
import com.wayfinder.core.id.NodeId;
import com.wayfinder.core.math.WorldPosition;
import com.wayfinder.geography.model.Landmark;
import com.wayfinder.geography.model.LandmarkType;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

final class RelationshipCommitServiceTest {

    @Test
    void acceptedRelationshipCommitsExactlyOneEdge() {
        CivilizationState state = stateWithTwoNodes();

        RelationshipProposal proposal = proposal(state);

        RelationshipAdmissionDecision admission =
                new RelationshipAdmissionService(
                        new RelationshipProposalValidator(
                                0.55
                        )
                ).evaluate(
                        proposal,
                        new RelationshipValidationContext(
                                state.nodes(),
                                state.relationships()
                        )
                );

        RelationshipTransition transition =
                new RelationshipTransitionFactory(
                        new RelationshipIdentityFactory()
                ).create(admission).orElseThrow();

        RelationshipCommitResult result =
                new RelationshipCommitService(
                        new RelationshipTransitionValidator()
                ).commit(
                        transition,
                        state
                );

        assertTrue(result.committed());
        assertEquals(
                1,
                result.state()
                        .relationships()
                        .size()
        );
    }

    @Test
    void duplicateRelationshipDoesNotMutateState() {
        CivilizationState state = stateWithTwoNodes();
        RelationshipProposal proposal = proposal(state);

        RelationshipAdmissionDecision admission =
                new RelationshipAdmissionService(
                        new RelationshipProposalValidator(
                                0.55
                        )
                ).evaluate(
                        proposal,
                        new RelationshipValidationContext(
                                state.nodes(),
                                state.relationships()
                        )
                );

        RelationshipTransition transition =
                new RelationshipTransitionFactory(
                        new RelationshipIdentityFactory()
                ).create(admission).orElseThrow();

        RelationshipCommitService service =
                new RelationshipCommitService(
                        new RelationshipTransitionValidator()
                );

        RelationshipCommitResult first =
                service.commit(
                        transition,
                        state
                );

        RelationshipCommitResult second =
                service.commit(
                        transition,
                        first.state()
                );

        assertTrue(first.committed());
        assertFalse(second.committed());
        assertSame(
                first.state(),
                second.state()
        );
        assertEquals(
                1,
                second.state()
                        .relationships()
                        .size()
        );
    }

    private static CivilizationState stateWithTwoNodes() {
        CivilizationNode direction =
                node(
                        "11111111-1111-1111-1111-111111111111",
                        NodePurpose.DIRECTION,
                        0
                );

        CivilizationNode observation =
                node(
                        "22222222-2222-2222-2222-222222222222",
                        NodePurpose.OBSERVATION,
                        80
                );

        return new CivilizationState(
                List.of(
                        direction,
                        observation
                )
        );
    }

    private static RelationshipProposal proposal(
            CivilizationState state
    ) {
        return new RelationshipProposal(
                "direction-to-observation",
                RelationshipType.DIRECTIONAL_REFERENCE,
                state.nodes().get(0).id(),
                state.nodes().get(1).id(),
                0.82,
                reasons()
        );
    }

    private static CivilizationNode node(
            String id,
            NodePurpose purpose,
            int x
    ) {
        Landmark landmark =
                new Landmark(
                        new GeographicFeatureId(
                                UUID.fromString(
                                        "33333333-3333-3333-3333-333333333333"
                                )
                        ),
                        new WorldPosition(
                                160,
                                100,
                                0
                        ),
                        LandmarkType.PROMINENT_PEAK,
                        0.90,
                        0.85,
                        0.75
                );

        NodeId nodeId =
                new NodeId(
                        UUID.fromString(id)
                );

        var semanticTarget =
                purpose == NodePurpose.DIRECTION
                        ? new CivilizationNodeTarget(
                                new NodeId(
                                        UUID.fromString(
                                                "22222222-2222-2222-2222-222222222222"
                                        )
                                )
                        )
                        : new GeographicNodeTarget(landmark);

        return new CivilizationNode(
                nodeId,
                purpose,
                new WorldPosition(
                        x,
                        80,
                        0
                ),
                semanticTarget,
                0.80,
                reasons(),
                "test-node-" + id
        );
    }

    private static ReasonEvaluation reasons() {
        return new ReasonEvaluation(
                new ReasonRecord(
                        ReasonType.COMMUNICATION,
                        0.90,
                        "The relationship communicates a purposeful destination."
                ),
                List.of(
                        new ReasonRecord(
                                ReasonType.CIVILIZATIONAL,
                                0.80,
                                "The connection extends a coherent Wayfinder network."
                        )
                )
        );
    }
}

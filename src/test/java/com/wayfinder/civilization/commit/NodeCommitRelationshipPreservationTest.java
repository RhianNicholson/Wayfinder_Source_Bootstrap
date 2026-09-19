package com.wayfinder.civilization.commit;

import com.wayfinder.civilization.model.NodePurpose;
import com.wayfinder.civilization.network.NodeAdmissionDecision;
import com.wayfinder.civilization.network.NodeProposal;
import com.wayfinder.civilization.network.NetworkValidationResult;
import com.wayfinder.civilization.reason.ReasonEvaluation;
import com.wayfinder.civilization.reason.ReasonRecord;
import com.wayfinder.civilization.reason.ReasonType;
import com.wayfinder.civilization.relationship.CivilizationRelationship;
import com.wayfinder.civilization.relationship.RelationshipType;
import com.wayfinder.civilization.state.CivilizationNode;
import com.wayfinder.civilization.state.CivilizationState;
import com.wayfinder.civilization.target.CivilizationNodeTarget;
import com.wayfinder.civilization.target.GeographicNodeTarget;
import com.wayfinder.core.id.GeographicFeatureId;
import com.wayfinder.core.id.NodeId;
import com.wayfinder.core.id.RelationshipId;
import com.wayfinder.core.math.WorldPosition;
import com.wayfinder.geography.model.Landmark;
import com.wayfinder.geography.model.LandmarkType;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

final class NodeCommitRelationshipPreservationTest {

    @Test
    void addingNodePreservesExistingRelationships() {
        CivilizationNode observation =
                observationNode();

        CivilizationNode direction =
                directionNode(
                        observation.id()
                );

        CivilizationRelationship relationship =
                new CivilizationRelationship(
                        new RelationshipId(
                                UUID.fromString(
                                        "55555555-5555-5555-5555-555555555555"
                                )
                        ),
                        RelationshipType.DIRECTIONAL_REFERENCE,
                        direction.id(),
                        observation.id(),
                        0.80,
                        reasons(),
                        "existing-relationship"
                );

        CivilizationState current =
                new CivilizationState(
                        List.of(
                                observation,
                                direction
                        ),
                        List.of(
                                relationship
                        )
                );

        NodeProposal proposal =
                new NodeProposal(
                        "DIR:new-node",
                        NodePurpose.DIRECTION,
                        new WorldPosition(
                                -80,
                                70,
                                0
                        ),
                        new CivilizationNodeTarget(
                                observation.id()
                        ),
                        0.82,
                        reasons()
                );

        NodeAdmissionDecision admission =
                new NodeAdmissionDecision(
                        proposal,
                        NetworkValidationResult.allow()
                );

        NodeTransition transition =
                new NodeTransitionFactory()
                        .create(admission)
                        .orElseThrow();

        CommitResult result =
                new NodeCommitService(
                        new NodeTransitionValidator()
                ).commit(
                        transition,
                        current
                );

        assertTrue(result.committed());
        assertEquals(
                3,
                result.state().nodes().size()
        );
        assertEquals(
                List.of(relationship),
                result.state()
                        .relationships()
        );
    }

    private static CivilizationNode observationNode() {
        return new CivilizationNode(
                new NodeId(
                        UUID.fromString(
                                "11111111-1111-1111-1111-111111111111"
                        )
                ),
                NodePurpose.OBSERVATION,
                new WorldPosition(
                        0,
                        70,
                        0
                ),
                new GeographicNodeTarget(
                        landmark()
                ),
                0.85,
                reasons(),
                "OBS:test"
        );
    }

    private static CivilizationNode directionNode(
            NodeId target
    ) {
        return new CivilizationNode(
                new NodeId(
                        UUID.fromString(
                                "22222222-2222-2222-2222-222222222222"
                        )
                ),
                NodePurpose.DIRECTION,
                new WorldPosition(
                        80,
                        70,
                        0
                ),
                new CivilizationNodeTarget(target),
                0.82,
                reasons(),
                "DIR:existing"
        );
    }

    private static Landmark landmark() {
        return new Landmark(
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
    }

    private static ReasonEvaluation reasons() {
        return new ReasonEvaluation(
                new ReasonRecord(
                        ReasonType.COMMUNICATION,
                        0.90,
                        "Test relationship reason."
                ),
                List.of(
                        new ReasonRecord(
                                ReasonType.CIVILIZATIONAL,
                                0.80,
                                "Test civilization reason."
                        )
                )
        );
    }
}

package com.wayfinder.civilization.persistence;

import com.wayfinder.civilization.model.NodePurpose;
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

final class CivilizationRelationshipPersistenceTest {

    @Test
    void relationshipRoundTripPreservesHistoricalTruth() {
        CivilizationNode source = node(
                "11111111-1111-1111-1111-111111111111",
                NodePurpose.DIRECTION,
                0
        );

        CivilizationNode target = node(
                "22222222-2222-2222-2222-222222222222",
                NodePurpose.OBSERVATION,
                80
        );

        CivilizationRelationship relationship =
                new CivilizationRelationship(
                        new RelationshipId(
                                UUID.fromString(
                                        "44444444-4444-4444-4444-444444444444"
                                )
                        ),
                        RelationshipType.DIRECTIONAL_REFERENCE,
                        source.id(),
                        target.id(),
                        0.82,
                        reasons(),
                        "direction-to-observation"
                );

        CivilizationState original =
                new CivilizationState(
                        List.of(
                                source,
                                target
                        ),
                        List.of(
                                relationship
                        )
                );

        var persistedNodes =
                CivilizationStatePersistenceMapper
                        .toPersisted(original);

        var persistedRelationships =
                CivilizationStatePersistenceMapper
                        .toPersistedRelationships(
                                original
                        );

        CivilizationState restored =
                CivilizationStatePersistenceMapper
                        .toDomain(
                                persistedNodes,
                                persistedRelationships
                        );

        assertEquals(
                original,
                restored
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

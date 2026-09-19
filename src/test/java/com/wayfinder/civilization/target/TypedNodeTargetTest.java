package com.wayfinder.civilization.target;

import com.wayfinder.civilization.model.NodePurpose;
import com.wayfinder.civilization.network.NodeProposalFactory;
import com.wayfinder.civilization.persistence.CivilizationStatePersistenceMapper;
import com.wayfinder.civilization.reason.ReasonEvaluation;
import com.wayfinder.civilization.reason.ReasonRecord;
import com.wayfinder.civilization.reason.ReasonType;
import com.wayfinder.civilization.state.CivilizationNode;
import com.wayfinder.civilization.state.CivilizationState;
import com.wayfinder.core.id.GeographicFeatureId;
import com.wayfinder.core.id.NodeId;
import com.wayfinder.core.math.WorldPosition;
import com.wayfinder.geography.model.Landmark;
import com.wayfinder.geography.model.LandmarkType;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

final class TypedNodeTargetTest {

    @Test
    void observationNodeUsesGeographicTarget() {
        CivilizationNode node = observation();

        assertInstanceOf(
                GeographicNodeTarget.class,
                node.target()
        );
        assertTrue(node.geographicTarget().isPresent());
        assertTrue(node.civilizationTarget().isEmpty());
    }

    @Test
    void directionNodeUsesCivilizationTarget() {
        CivilizationNode destination = observation();

        CivilizationNode direction =
                new CivilizationNode(
                        new NodeId(
                                UUID.fromString(
                                        "44444444-4444-4444-4444-444444444444"
                                )
                        ),
                        NodePurpose.DIRECTION,
                        new WorldPosition(
                                20,
                                70,
                                0
                        ),
                        new CivilizationNodeTarget(
                                destination.id()
                        ),
                        0.80,
                        reasons(),
                        "DIR:test"
                );

        assertInstanceOf(
                CivilizationNodeTarget.class,
                direction.target()
        );
        assertEquals(
                destination.id(),
                direction.civilizationTarget()
                        .orElseThrow()
        );
    }

    @Test
    void directionCannotPretendLandmarkIsItsSemanticTarget() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new CivilizationNode(
                        new NodeId(UUID.randomUUID()),
                        NodePurpose.DIRECTION,
                        new WorldPosition(0, 70, 0),
                        landmark(),
                        0.80,
                        reasons(),
                        "DIR:invalid"
                )
        );
    }

    @Test
    void bothTargetKindsRoundTripThroughPlainPersistenceMapper() {
        CivilizationNode observation = observation();

        CivilizationNode direction =
                new CivilizationNode(
                        new NodeId(
                                UUID.fromString(
                                        "55555555-5555-5555-5555-555555555555"
                                )
                        ),
                        NodePurpose.DIRECTION,
                        new WorldPosition(
                                20,
                                70,
                                0
                        ),
                        new CivilizationNodeTarget(
                                observation.id()
                        ),
                        0.81,
                        reasons(),
                        "DIR:roundtrip"
                );

        CivilizationState original =
                new CivilizationState(
                        List.of(
                                observation,
                                direction
                        )
                );

        var persisted =
                CivilizationStatePersistenceMapper
                        .toPersisted(original);

        CivilizationState restored =
                CivilizationStatePersistenceMapper
                        .toDomain(persisted);

        assertEquals(original, restored);
    }

    private static CivilizationNode observation() {
        return new CivilizationNode(
                new NodeId(
                        UUID.fromString(
                                "22222222-2222-2222-2222-222222222222"
                        )
                ),
                NodePurpose.OBSERVATION,
                new WorldPosition(80, 76, 0),
                landmark(),
                0.84,
                reasons(),
                "OBS:test"
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
                0.91,
                0.82,
                0.73
        );
    }

    private static ReasonEvaluation reasons() {
        return new ReasonEvaluation(
                new ReasonRecord(
                        ReasonType.COMMUNICATION,
                        0.88,
                        "The target communicates a meaningful relationship."
                ),
                List.of()
        );
    }
}

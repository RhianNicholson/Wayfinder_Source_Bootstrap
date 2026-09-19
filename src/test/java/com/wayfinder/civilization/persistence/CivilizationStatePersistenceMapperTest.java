package com.wayfinder.civilization.persistence;

import com.wayfinder.civilization.model.NodePurpose;
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

import static org.junit.jupiter.api.Assertions.assertEquals;

final class CivilizationStatePersistenceMapperTest {

    @Test
    void roundTripPreservesAuthoritativeCivilizationTruth() {
        CivilizationState original = new CivilizationState(
                List.of(node())
        );

        var persisted =
                CivilizationStatePersistenceMapper.toPersisted(original);
        CivilizationState restored =
                CivilizationStatePersistenceMapper.toDomain(persisted);

        assertEquals(original, restored);
    }

    private static CivilizationNode node() {
        Landmark landmark = new Landmark(
                new GeographicFeatureId(
                        UUID.fromString(
                                "11111111-1111-1111-1111-111111111111"
                        )
                ),
                new WorldPosition(100, 101, 102),
                LandmarkType.PROMINENT_PEAK,
                0.91,
                0.82,
                0.73
        );

        ReasonEvaluation reasons = new ReasonEvaluation(
                new ReasonRecord(
                        ReasonType.OBSERVATION,
                        0.88,
                        "The site preserves a meaningful sightline."
                ),
                List.of(
                        new ReasonRecord(
                                ReasonType.GEOGRAPHIC,
                                0.76,
                                "The terrain creates a plausible observation site."
                        )
                )
        );

        return new CivilizationNode(
                new NodeId(
                        UUID.fromString(
                                "22222222-2222-2222-2222-222222222222"
                        )
                ),
                NodePurpose.OBSERVATION,
                new WorldPosition(20, 80, 30),
                landmark,
                0.84,
                reasons,
                "OBS:test-proposal"
        );
    }
}

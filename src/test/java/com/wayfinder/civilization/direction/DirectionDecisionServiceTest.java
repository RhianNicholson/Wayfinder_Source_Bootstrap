package com.wayfinder.civilization.direction;

import com.wayfinder.civilization.model.NodePurpose;
import com.wayfinder.civilization.reason.ReasonEvaluation;
import com.wayfinder.civilization.reason.ReasonRecord;
import com.wayfinder.civilization.reason.ReasonType;
import com.wayfinder.civilization.state.CivilizationNode;
import com.wayfinder.core.id.GeographicFeatureId;
import com.wayfinder.core.id.NodeId;
import com.wayfinder.core.math.WorldPosition;
import com.wayfinder.geography.model.Landmark;
import com.wayfinder.geography.model.LandmarkType;
import com.wayfinder.geography.world.WorldTerrainView;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

final class DirectionDecisionServiceTest {

    @Test
    void createsPurposefulDirectionDecisionTowardCommittedObservationNode() {
        CivilizationNode observation =
                node(
                        NodePurpose.OBSERVATION,
                        new WorldPosition(0, 70, 0)
                );

        DirectionDecisionService service =
                service(new FlatTerrain());

        DirectionDecision decision =
                service.decide(
                        observation,
                        List.of(observation)
                ).orElseThrow();

        assertEquals(
                NodePurpose.DIRECTION,
                decision.purpose()
        );
        assertEquals(
                observation.id(),
                decision.selected()
                        .candidate()
                        .destination()
                        .id()
        );
        assertTrue(
                decision.generatedCandidates() > 0
        );
        assertTrue(
                decision.validCandidates() > 0
        );
        assertTrue(
                decision.plausibleCandidates() > 0
        );
        assertTrue(
                decision.selected()
                        .score()
                        .finalScore() >= 0.55
        );
    }

    @Test
    void nonObservationNodeCannotCreateDirectionDecision() {
        CivilizationNode record =
                node(
                        NodePurpose.RECORD,
                        new WorldPosition(0, 70, 0)
                );

        assertTrue(
                service(new FlatTerrain())
                        .decide(
                                record,
                                List.of(record)
                        )
                        .isEmpty()
        );
    }

    @Test
    void existingNearbyNodeInvalidatesOtherwiseGoodCandidate() {
        CivilizationNode observation =
                node(
                        NodePurpose.OBSERVATION,
                        new WorldPosition(0, 70, 0)
                );

        DirectionCandidate candidate =
                new DirectionCandidate(
                        "test",
                        new WorldPosition(80, 70, 0),
                        observation,
                        80.0,
                        1.0,
                        1.0,
                        1.0
                );

        CivilizationNode blocker =
                node(
                        NodePurpose.RECORD,
                        new WorldPosition(82, 70, 0)
                );

        DirectionCandidateValidator validator =
                new DirectionCandidateValidator(
                        0.35,
                        40.0,
                        160.0,
                        16.0
                );

        assertFalse(
                validator.isValid(
                        candidate,
                        List.of(
                                observation,
                                blocker
                        )
                )
        );
    }

    private static DirectionDecisionService service(
            WorldTerrainView world
    ) {
        return new DirectionDecisionService(
                new DirectionCandidateGenerator(world),
                new DirectionCandidateValidator(
                        0.35,
                        40.0,
                        160.0,
                        16.0
                ),
                new DirectionReasonEvaluator(),
                new DirectionCandidateScorer(),
                new DirectionPlausibilityFilter(
                        0.55,
                        0.85
                ),
                new DeterministicDirectionSelector()
        );
    }

    private static CivilizationNode node(
            NodePurpose purpose,
            WorldPosition position
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

        return new CivilizationNode(
                new NodeId(UUID.randomUUID()),
                purpose,
                position,
                landmark,
                0.82,
                new ReasonEvaluation(
                        new ReasonRecord(
                                ReasonType.OBSERVATION,
                                0.90,
                                "Test node."
                        ),
                        List.of()
                ),
                "test-" + purpose + "-" + position.x()
        );
    }

    private static final class FlatTerrain
            implements WorldTerrainView {

        @Override
        public int surfaceHeight(int x, int z) {
            return 70;
        }

        @Override
        public boolean isWater(
                int x,
                int y,
                int z
        ) {
            return false;
        }

        @Override
        public boolean isSolid(
                int x,
                int y,
                int z
        ) {
            return y <= 70;
        }
    }
}

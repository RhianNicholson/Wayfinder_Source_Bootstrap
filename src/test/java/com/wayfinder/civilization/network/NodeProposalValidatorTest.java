package com.wayfinder.civilization.network;

import com.wayfinder.civilization.model.NodePurpose;
import com.wayfinder.civilization.reason.ReasonEvaluation;
import com.wayfinder.civilization.reason.ReasonRecord;
import com.wayfinder.civilization.reason.ReasonType;
import com.wayfinder.core.id.GeographicFeatureId;
import com.wayfinder.core.id.NodeId;
import com.wayfinder.core.math.WorldPosition;
import com.wayfinder.geography.model.Landmark;
import com.wayfinder.geography.model.LandmarkType;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

final class NodeProposalValidatorTest {

    @Test
    void acceptsPurposefulObservationInEmptyNetwork() {
        var validator = new NodeProposalValidator(0.55, 12.0, 48.0);

        var result = validator.validate(
                proposal(new WorldPosition(0, 80, 0), 0.80),
                NetworkValidationContext.empty()
        );

        assertTrue(result.accepted());
        assertTrue(result.issues().isEmpty());
    }

    @Test
    void rejectsRedundantNearbyObservation() {
        var validator = new NodeProposalValidator(0.55, 12.0, 48.0);

        var existing = new CommittedNodeSnapshot(
                new NodeId(UUID.fromString("22222222-2222-2222-2222-222222222222")),
                NodePurpose.OBSERVATION,
                new WorldPosition(25, 80, 0)
        );

        var result = validator.validate(
                proposal(new WorldPosition(0, 80, 0), 0.80),
                new NetworkValidationContext(List.of(existing))
        );

        assertFalse(result.accepted());
        assertEquals(
                NetworkValidationCode.OBSERVATION_REDUNDANCY,
                result.issues().get(0).code()
        );
    }

    @Test
    void rejectsProposalBelowAdmissionScore() {
        var validator = new NodeProposalValidator(0.55, 12.0, 48.0);

        var result = validator.validate(
                proposal(new WorldPosition(0, 80, 0), 0.40),
                NetworkValidationContext.empty()
        );

        assertFalse(result.accepted());
        assertEquals(
                NetworkValidationCode.INVALID_PROPOSAL_SCORE,
                result.issues().get(0).code()
        );
    }

    private static NodeProposal proposal(WorldPosition position, double score) {
        var target = new Landmark(
                new GeographicFeatureId(
                        UUID.fromString("11111111-1111-1111-1111-111111111111")
                ),
                new WorldPosition(100, 100, 0),
                LandmarkType.PROMINENT_PEAK,
                0.9,
                0.9,
                0.8
        );

        var reasons = new ReasonEvaluation(
                new ReasonRecord(
                        ReasonType.OBSERVATION,
                        0.9,
                        "The site provides a useful sightline."
                ),
                List.of(
                        new ReasonRecord(
                                ReasonType.GEOGRAPHIC,
                                0.8,
                                "Terrain creates the opportunity."
                        )
                )
        );

        return new NodeProposal(
                "test-proposal",
                NodePurpose.OBSERVATION,
                position,
                target,
                score,
                reasons
        );
    }
}

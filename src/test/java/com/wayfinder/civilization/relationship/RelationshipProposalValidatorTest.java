package com.wayfinder.civilization.relationship;

import com.wayfinder.civilization.model.NodePurpose;
import com.wayfinder.civilization.reason.ReasonEvaluation;
import com.wayfinder.civilization.reason.ReasonRecord;
import com.wayfinder.civilization.reason.ReasonType;
import com.wayfinder.civilization.state.CivilizationNode;
import com.wayfinder.civilization.target.CivilizationNodeTarget;
import com.wayfinder.core.id.GeographicFeatureId;
import com.wayfinder.core.id.NodeId;
import com.wayfinder.core.math.WorldPosition;
import com.wayfinder.geography.model.Landmark;
import com.wayfinder.geography.model.LandmarkType;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

final class RelationshipProposalValidatorTest {

    private static final NodeId DIRECTION_ID =
            new NodeId(
                    UUID.fromString(
                            "11111111-1111-1111-1111-111111111111"
                    )
            );

    private static final NodeId OBSERVATION_ID =
            new NodeId(
                    UUID.fromString(
                            "22222222-2222-2222-2222-222222222222"
                    )
            );

    @Test
    void acceptsDirectionToObservationRelationship() {
        RelationshipProposalValidator validator =
                new RelationshipProposalValidator(0.55);

        RelationshipValidationResult result =
                validator.validate(
                        proposal(
                                DIRECTION_ID,
                                OBSERVATION_ID,
                                0.82
                        ),
                        RelationshipValidationContext.ofNodes(
                                List.of(
                                        node(
                                                DIRECTION_ID,
                                                NodePurpose.DIRECTION,
                                                0
                                        ),
                                        node(
                                                OBSERVATION_ID,
                                                NodePurpose.OBSERVATION,
                                                80
                                        )
                                )
                        )
                );

        assertTrue(result.accepted());
        assertTrue(result.issues().isEmpty());
    }

    @Test
    void rejectsObservationToDirectionForDirectionalReference() {
        RelationshipProposalValidator validator =
                new RelationshipProposalValidator(0.55);

        RelationshipValidationResult result =
                validator.validate(
                        proposal(
                                OBSERVATION_ID,
                                DIRECTION_ID,
                                0.82
                        ),
                        RelationshipValidationContext.ofNodes(
                                List.of(
                                        node(
                                                DIRECTION_ID,
                                                NodePurpose.DIRECTION,
                                                0
                                        ),
                                        node(
                                                OBSERVATION_ID,
                                                NodePurpose.OBSERVATION,
                                                80
                                        )
                                )
                        )
                );

        assertFalse(result.accepted());
        assertEquals(
                RelationshipValidationCode.INCOMPATIBLE_NODE_PURPOSES,
                result.issues().get(0).code()
        );
    }

    @Test
    void rejectsRelationshipToMissingNode() {
        RelationshipProposalValidator validator =
                new RelationshipProposalValidator(0.55);

        RelationshipValidationResult result =
                validator.validate(
                        proposal(
                                DIRECTION_ID,
                                OBSERVATION_ID,
                                0.82
                        ),
                        RelationshipValidationContext.ofNodes(
                                List.of(
                                        node(
                                                DIRECTION_ID,
                                                NodePurpose.DIRECTION,
                                                0
                                        )
                                )
                        )
                );

        assertFalse(result.accepted());
        assertEquals(
                RelationshipValidationCode.TARGET_NODE_MISSING,
                result.issues().get(0).code()
        );
    }

    @Test
    void deterministicIdentityIsStableForSameProposal() {
        RelationshipIdentityFactory factory =
                new RelationshipIdentityFactory();

        RelationshipProposal proposal =
                proposal(
                        DIRECTION_ID,
                        OBSERVATION_ID,
                        0.82
                );

        assertEquals(
                factory.fromProposal(proposal),
                factory.fromProposal(proposal)
        );
    }

    private static RelationshipProposal proposal(
            NodeId source,
            NodeId target,
            double score
    ) {
        return new RelationshipProposal(
                "direction-to-observation",
                RelationshipType.DIRECTIONAL_REFERENCE,
                source,
                target,
                score,
                new ReasonEvaluation(
                        new ReasonRecord(
                                ReasonType.COMMUNICATION,
                                0.90,
                                "The source intentionally communicates the location of the observation node."
                        ),
                        List.of(
                                new ReasonRecord(
                                        ReasonType.CIVILIZATIONAL,
                                        0.80,
                                        "The relationship extends a coherent Wayfinder navigation chain."
                                )
                        )
                )
        );
    }

    private static CivilizationNode node(
            NodeId id,
            NodePurpose purpose,
            int x
    ) {
        Landmark target = new Landmark(
                new GeographicFeatureId(
                        UUID.fromString(
                                "33333333-3333-3333-3333-333333333333"
                        )
                ),
                new WorldPosition(160, 100, 0),
                LandmarkType.PROMINENT_PEAK,
                0.90,
                0.85,
                0.75
        );

        var semanticTarget =
                purpose == NodePurpose.DIRECTION
                        ? new CivilizationNodeTarget(OBSERVATION_ID)
                        : new com.wayfinder.civilization.target.GeographicNodeTarget(target);

        return new CivilizationNode(
                id,
                purpose,
                new WorldPosition(x, 80, 0),
                semanticTarget,
                0.80,
                new ReasonEvaluation(
                        new ReasonRecord(
                                ReasonType.CIVILIZATIONAL,
                                0.85,
                                "Committed node exists for a purposeful civilization function."
                        ),
                        List.of()
                ),
                "test-node-" + id.value()
        );
    }
}

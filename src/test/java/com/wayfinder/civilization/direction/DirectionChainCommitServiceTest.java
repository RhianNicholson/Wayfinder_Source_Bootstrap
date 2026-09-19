package com.wayfinder.civilization.direction;

import com.wayfinder.civilization.commit.NodeCommitService;
import com.wayfinder.civilization.commit.NodeTransitionFactory;
import com.wayfinder.civilization.commit.NodeTransitionValidator;
import com.wayfinder.civilization.model.NodePurpose;
import com.wayfinder.civilization.network.NodeAdmissionService;
import com.wayfinder.civilization.network.NodeProposalFactory;
import com.wayfinder.civilization.network.NodeProposalValidator;
import com.wayfinder.civilization.reason.ReasonEvaluation;
import com.wayfinder.civilization.reason.ReasonRecord;
import com.wayfinder.civilization.reason.ReasonType;
import com.wayfinder.civilization.relationship.DirectionalReferenceProposalFactory;
import com.wayfinder.civilization.relationship.RelationshipAdmissionService;
import com.wayfinder.civilization.relationship.RelationshipCommitService;
import com.wayfinder.civilization.relationship.RelationshipIdentityFactory;
import com.wayfinder.civilization.relationship.RelationshipProposalValidator;
import com.wayfinder.civilization.relationship.RelationshipTransitionFactory;
import com.wayfinder.civilization.relationship.RelationshipTransitionValidator;
import com.wayfinder.civilization.relationship.RelationshipType;
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

final class DirectionChainCommitServiceTest {

    @Test
    void commitsDirectionNodeAndDirectionalReferenceTogether() {
        CivilizationNode observation =
                observationNode();

        DirectionCandidate candidate =
                new DirectionCandidate(
                        "DIR:test-chain",
                        new WorldPosition(
                                80,
                                70,
                                0
                        ),
                        observation,
                        80.0,
                        1.0,
                        1.0,
                        1.0
                );

        ReasonEvaluation reasons =
                reasons();

        DirectionDecision decision =
                new DirectionDecision(
                        NodePurpose.DIRECTION,
                        new ScoredDirectionCandidate(
                                candidate,
                                new DirectionCandidateScore(
                                        0.86,
                                        1.0,
                                        1.0,
                                        1.0,
                                        observation.score()
                                ),
                                reasons
                        ),
                        1,
                        1,
                        1
                );

        DirectionChainCommitResult result =
                service().commit(
                        decision,
                        new CivilizationState(
                                List.of(observation)
                        )
                );

        assertTrue(result.committed());
        assertEquals(
                "COMMITTED",
                result.stage()
        );
        assertEquals(
                2,
                result.state().nodes().size()
        );
        assertEquals(
                1,
                result.state()
                        .relationships()
                        .size()
        );

        CivilizationNode direction =
                result.state().nodes()
                        .stream()
                        .filter(node ->
                                node.purpose()
                                        == NodePurpose.DIRECTION
                        )
                        .findFirst()
                        .orElseThrow();

        assertEquals(
                observation.id(),
                direction.civilizationTarget()
                        .orElseThrow()
        );

        var relationship =
                result.state()
                        .relationships()
                        .get(0);

        assertEquals(
                RelationshipType.DIRECTIONAL_REFERENCE,
                relationship.type()
        );
        assertEquals(
                direction.id(),
                relationship.sourceNodeId()
        );
        assertEquals(
                observation.id(),
                relationship.targetNodeId()
        );
    }

    private static DirectionChainCommitService service() {
        return new DirectionChainCommitService(
                new NodeAdmissionService(
                        new NodeProposalFactory(),
                        new NodeProposalValidator(
                                0.55,
                                12.0,
                                48.0
                        )
                ),
                new NodeTransitionFactory(),
                new NodeCommitService(
                        new NodeTransitionValidator()
                ),
                new DirectionalReferenceProposalFactory(),
                new RelationshipAdmissionService(
                        new RelationshipProposalValidator(
                                0.55
                        )
                ),
                new RelationshipTransitionFactory(
                        new RelationshipIdentityFactory()
                ),
                new RelationshipCommitService(
                        new RelationshipTransitionValidator()
                )
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
                landmark(),
                0.84,
                new ReasonEvaluation(
                        new ReasonRecord(
                                ReasonType.OBSERVATION,
                                0.90,
                                "Observation site exists."
                        ),
                        List.of()
                ),
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
                        "The Direction node communicates the Observation destination."
                ),
                List.of(
                        new ReasonRecord(
                                ReasonType.TRAVEL,
                                0.85,
                                "The location forms a plausible approach."
                        ),
                        new ReasonRecord(
                                ReasonType.CIVILIZATIONAL,
                                0.84,
                                "The destination is already committed civilization history."
                        )
                )
        );
    }
}

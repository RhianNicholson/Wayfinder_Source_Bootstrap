package com.wayfinder.civilization.commit;

import com.wayfinder.civilization.model.NodePurpose;
import com.wayfinder.civilization.network.NodeProposal;
import com.wayfinder.civilization.reason.ReasonEvaluation;
import com.wayfinder.civilization.reason.ReasonRecord;
import com.wayfinder.civilization.reason.ReasonType;
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

final class NodeCommitServiceTest {

    @Test
    void acceptedTransitionCommitsExactlyOneNode() {
        var service = new NodeCommitService(new NodeTransitionValidator());
        var transition = transition("proposal-a");

        var result = service.commit(
                transition,
                CivilizationState.empty()
        );

        assertTrue(result.committed());
        assertEquals(1, result.state().nodes().size());
        assertEquals(
                transition.node().id(),
                result.state().nodes().get(0).id()
        );
    }

    @Test
    void duplicateTransitionDoesNotMutateState() {
        var service = new NodeCommitService(new NodeTransitionValidator());
        var transition = transition("proposal-a");

        var first = service.commit(
                transition,
                CivilizationState.empty()
        );

        var second = service.commit(
                transition,
                first.state()
        );

        assertFalse(second.committed());
        assertSame(first.state(), second.state());
        assertEquals(1, second.state().nodes().size());
    }

    private static NodeTransition transition(String proposalKey) {
        var target = new Landmark(
                new GeographicFeatureId(
                        UUID.fromString(
                                "11111111-1111-1111-1111-111111111111"
                        )
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
                        "Useful observation relationship."
                ),
                List.of()
        );

        var proposal = new NodeProposal(
                proposalKey,
                NodePurpose.OBSERVATION,
                new WorldPosition(0, 80, 0),
                target,
                0.8,
                reasons
        );

        var node = new com.wayfinder.civilization.state.CivilizationNode(
                new NodeId(
                        UUID.fromString(
                                "22222222-2222-2222-2222-222222222222"
                        )
                ),
                NodePurpose.OBSERVATION,
                proposal.position(),
                target,
                proposal.score(),
                reasons,
                proposalKey
        );

        return new NodeTransition(proposal, node);
    }
}

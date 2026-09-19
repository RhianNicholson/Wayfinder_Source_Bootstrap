package com.wayfinder.civilization.commit;

import com.wayfinder.civilization.network.NodeAdmissionDecision;
import com.wayfinder.civilization.state.CivilizationNode;
import com.wayfinder.core.id.NodeId;

import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.UUID;

public final class NodeTransitionFactory {

    public Optional<NodeTransition> create(NodeAdmissionDecision admission) {
        if (!admission.accepted()) {
            return Optional.empty();
        }

        var proposal = admission.proposal();
        NodeId id = deterministicNodeId(proposal.proposalKey());

        CivilizationNode node = new CivilizationNode(
                id,
                proposal.purpose(),
                proposal.position(),
                proposal.target(),
                proposal.score(),
                proposal.reasons(),
                proposal.proposalKey()
        );

        return Optional.of(new NodeTransition(proposal, node));
    }

    private NodeId deterministicNodeId(String proposalKey) {
        UUID uuid = UUID.nameUUIDFromBytes(
                ("wayfinder-node:" + proposalKey).getBytes(StandardCharsets.UTF_8)
        );
        return new NodeId(uuid);
    }
}

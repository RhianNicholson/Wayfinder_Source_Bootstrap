package com.wayfinder.civilization.state;

import com.wayfinder.civilization.model.NodePurpose;
import com.wayfinder.civilization.reason.ReasonEvaluation;
import com.wayfinder.civilization.target.CivilizationNodeTarget;
import com.wayfinder.civilization.target.GeographicNodeTarget;
import com.wayfinder.civilization.target.NodeTarget;
import com.wayfinder.core.id.NodeId;
import com.wayfinder.core.math.WorldPosition;
import com.wayfinder.geography.model.Landmark;

import java.util.Optional;

public record CivilizationNode(
        NodeId id,
        NodePurpose purpose,
        WorldPosition position,
        NodeTarget target,
        double score,
        ReasonEvaluation reasons,
        String sourceProposalKey
) {
    public CivilizationNode {
        if (id == null) throw new IllegalArgumentException("id is required");
        if (purpose == null) throw new IllegalArgumentException("purpose is required");
        if (position == null) throw new IllegalArgumentException("position is required");
        if (target == null) throw new IllegalArgumentException("target is required");
        if (reasons == null) throw new IllegalArgumentException("reasons are required");
        if (sourceProposalKey == null || sourceProposalKey.isBlank()) {
            throw new IllegalArgumentException("sourceProposalKey is required");
        }
        if (score < 0.0 || score > 1.0) {
            throw new IllegalArgumentException("score must be 0..1");
        }

        validateTargetForPurpose(purpose, target);
    }

    /**
     * Source-compatible constructor for all pre-M13 Observation-node call
     * sites. A Landmark is explicitly promoted to a geographic target.
     */
    public CivilizationNode(
            NodeId id,
            NodePurpose purpose,
            WorldPosition position,
            Landmark target,
            double score,
            ReasonEvaluation reasons,
            String sourceProposalKey
    ) {
        this(
                id,
                purpose,
                position,
                new GeographicNodeTarget(target),
                score,
                reasons,
                sourceProposalKey
        );
    }

    public Optional<Landmark> geographicTarget() {
        if (target instanceof GeographicNodeTarget geographic) {
            return Optional.of(geographic.landmark());
        }

        return Optional.empty();
    }

    public Optional<NodeId> civilizationTarget() {
        if (target instanceof CivilizationNodeTarget civilization) {
            return Optional.of(civilization.nodeId());
        }

        return Optional.empty();
    }

    private static void validateTargetForPurpose(
            NodePurpose purpose,
            NodeTarget target
    ) {
        if (purpose == NodePurpose.OBSERVATION
                && !(target instanceof GeographicNodeTarget)) {
            throw new IllegalArgumentException(
                    "OBSERVATION nodes must target geography"
            );
        }

        if (purpose == NodePurpose.DIRECTION
                && !(target instanceof CivilizationNodeTarget)) {
            throw new IllegalArgumentException(
                    "DIRECTION nodes must target a civilization node"
            );
        }
    }
}

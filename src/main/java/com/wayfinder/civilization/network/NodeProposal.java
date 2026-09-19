package com.wayfinder.civilization.network;

import com.wayfinder.civilization.model.NodePurpose;
import com.wayfinder.civilization.reason.ReasonEvaluation;
import com.wayfinder.civilization.target.CivilizationNodeTarget;
import com.wayfinder.civilization.target.GeographicNodeTarget;
import com.wayfinder.civilization.target.NodeTarget;
import com.wayfinder.core.math.WorldPosition;
import com.wayfinder.geography.model.Landmark;

public record NodeProposal(
        String proposalKey,
        NodePurpose purpose,
        WorldPosition position,
        NodeTarget target,
        double score,
        ReasonEvaluation reasons
) {
    public NodeProposal {
        if (proposalKey == null || proposalKey.isBlank()) {
            throw new IllegalArgumentException("proposalKey is required");
        }
        if (purpose == null) throw new IllegalArgumentException("purpose is required");
        if (position == null) throw new IllegalArgumentException("position is required");
        if (target == null) throw new IllegalArgumentException("target is required");
        if (reasons == null) throw new IllegalArgumentException("reasons are required");
        if (score < 0.0 || score > 1.0) {
            throw new IllegalArgumentException("score must be 0..1");
        }

        if (purpose == NodePurpose.OBSERVATION
                && !(target instanceof GeographicNodeTarget)) {
            throw new IllegalArgumentException(
                    "OBSERVATION proposals must target geography"
            );
        }

        if (purpose == NodePurpose.DIRECTION
                && !(target instanceof CivilizationNodeTarget)) {
            throw new IllegalArgumentException(
                    "DIRECTION proposals must target a civilization node"
            );
        }
    }

    /**
     * Backward-compatible constructor for existing Observation proposals.
     */
    public NodeProposal(
            String proposalKey,
            NodePurpose purpose,
            WorldPosition position,
            Landmark target,
            double score,
            ReasonEvaluation reasons
    ) {
        this(
                proposalKey,
                purpose,
                position,
                new GeographicNodeTarget(target),
                score,
                reasons
        );
    }
}

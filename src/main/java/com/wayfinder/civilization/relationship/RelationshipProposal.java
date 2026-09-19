package com.wayfinder.civilization.relationship;

import com.wayfinder.civilization.reason.ReasonEvaluation;
import com.wayfinder.core.id.NodeId;

public record RelationshipProposal(
        String proposalKey,
        RelationshipType type,
        NodeId sourceNodeId,
        NodeId targetNodeId,
        double score,
        ReasonEvaluation reasons
) {
    public RelationshipProposal {
        if (proposalKey == null || proposalKey.isBlank()) {
            throw new IllegalArgumentException("proposalKey is required");
        }
        if (type == null) throw new IllegalArgumentException("type is required");
        if (sourceNodeId == null) {
            throw new IllegalArgumentException("sourceNodeId is required");
        }
        if (targetNodeId == null) {
            throw new IllegalArgumentException("targetNodeId is required");
        }
        if (reasons == null) throw new IllegalArgumentException("reasons are required");
        if (score < 0.0 || score > 1.0) {
            throw new IllegalArgumentException("score must be 0..1");
        }
    }
}

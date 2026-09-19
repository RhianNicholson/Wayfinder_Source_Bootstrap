package com.wayfinder.civilization.relationship;

import com.wayfinder.civilization.reason.ReasonEvaluation;
import com.wayfinder.core.id.NodeId;
import com.wayfinder.core.id.RelationshipId;

public record CivilizationRelationship(
        RelationshipId id,
        RelationshipType type,
        NodeId sourceNodeId,
        NodeId targetNodeId,
        double score,
        ReasonEvaluation reasons,
        String sourceProposalKey
) {
    public CivilizationRelationship {
        if (id == null) throw new IllegalArgumentException("id is required");
        if (type == null) throw new IllegalArgumentException("type is required");
        if (sourceNodeId == null) {
            throw new IllegalArgumentException("sourceNodeId is required");
        }
        if (targetNodeId == null) {
            throw new IllegalArgumentException("targetNodeId is required");
        }
        if (reasons == null) throw new IllegalArgumentException("reasons are required");
        if (sourceProposalKey == null || sourceProposalKey.isBlank()) {
            throw new IllegalArgumentException("sourceProposalKey is required");
        }
        if (score < 0.0 || score > 1.0) {
            throw new IllegalArgumentException("score must be 0..1");
        }
    }
}

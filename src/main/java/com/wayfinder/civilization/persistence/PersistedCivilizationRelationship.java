package com.wayfinder.civilization.persistence;

import com.wayfinder.civilization.relationship.CivilizationRelationship;
import com.wayfinder.civilization.relationship.RelationshipType;
import com.wayfinder.core.id.NodeId;
import com.wayfinder.core.id.RelationshipId;

import java.util.UUID;

public record PersistedCivilizationRelationship(
        String id,
        String type,
        String sourceNodeId,
        String targetNodeId,
        double score,
        PersistedReasonEvaluation reasons,
        String sourceProposalKey
) {
    public static PersistedCivilizationRelationship fromDomain(
            CivilizationRelationship relationship
    ) {
        return new PersistedCivilizationRelationship(
                relationship.id().value().toString(),
                relationship.type().name(),
                relationship.sourceNodeId().value().toString(),
                relationship.targetNodeId().value().toString(),
                relationship.score(),
                PersistedReasonEvaluation.fromDomain(
                        relationship.reasons()
                ),
                relationship.sourceProposalKey()
        );
    }

    public CivilizationRelationship toDomain() {
        return new CivilizationRelationship(
                new RelationshipId(
                        UUID.fromString(id)
                ),
                RelationshipType.valueOf(type),
                new NodeId(
                        UUID.fromString(sourceNodeId)
                ),
                new NodeId(
                        UUID.fromString(targetNodeId)
                ),
                score,
                reasons.toDomain(),
                sourceProposalKey
        );
    }
}

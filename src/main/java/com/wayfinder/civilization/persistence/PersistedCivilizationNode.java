package com.wayfinder.civilization.persistence;

import com.wayfinder.civilization.model.NodePurpose;
import com.wayfinder.civilization.state.CivilizationNode;
import com.wayfinder.civilization.target.CivilizationNodeTarget;
import com.wayfinder.civilization.target.GeographicNodeTarget;
import com.wayfinder.core.id.NodeId;

import java.util.Optional;
import java.util.UUID;

public record PersistedCivilizationNode(
        String id,
        String purpose,
        PersistedPosition position,
        Optional<PersistedLandmark> geographicTarget,
        Optional<String> civilizationTargetNodeId,
        double score,
        PersistedReasonEvaluation reasons,
        String sourceProposalKey
) {
    public PersistedCivilizationNode {
        geographicTarget = geographicTarget == null
                ? Optional.empty()
                : geographicTarget;
        civilizationTargetNodeId =
                civilizationTargetNodeId == null
                        ? Optional.empty()
                        : civilizationTargetNodeId;

        if (geographicTarget.isPresent()
                == civilizationTargetNodeId.isPresent()) {
            throw new IllegalArgumentException(
                    "Exactly one persisted node target must be present"
            );
        }
    }

    public static PersistedCivilizationNode fromDomain(
            CivilizationNode node
    ) {
        Optional<PersistedLandmark> geographic =
                node.target() instanceof GeographicNodeTarget target
                        ? Optional.of(
                                PersistedLandmark.fromDomain(
                                        target.landmark()
                                )
                        )
                        : Optional.empty();

        Optional<String> civilization =
                node.target() instanceof CivilizationNodeTarget target
                        ? Optional.of(
                                target.nodeId()
                                        .value()
                                        .toString()
                        )
                        : Optional.empty();

        return new PersistedCivilizationNode(
                node.id().value().toString(),
                node.purpose().name(),
                PersistedPosition.fromDomain(
                        node.position()
                ),
                geographic,
                civilization,
                node.score(),
                PersistedReasonEvaluation.fromDomain(
                        node.reasons()
                ),
                node.sourceProposalKey()
        );
    }

    public CivilizationNode toDomain() {
        var target = geographicTarget
                .<com.wayfinder.civilization.target.NodeTarget>map(
                        persisted ->
                                new GeographicNodeTarget(
                                        persisted.toDomain()
                                )
                )
                .orElseGet(
                        () -> new CivilizationNodeTarget(
                                new NodeId(
                                        UUID.fromString(
                                                civilizationTargetNodeId
                                                        .orElseThrow()
                                        )
                                )
                        )
                );

        return new CivilizationNode(
                new NodeId(UUID.fromString(id)),
                NodePurpose.valueOf(purpose),
                position.toDomain(),
                target,
                score,
                reasons.toDomain(),
                sourceProposalKey
        );
    }
}

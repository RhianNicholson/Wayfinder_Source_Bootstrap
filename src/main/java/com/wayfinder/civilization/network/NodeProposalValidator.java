package com.wayfinder.civilization.network;

import com.wayfinder.civilization.model.NodePurpose;
import com.wayfinder.civilization.reason.ReasonType;
import com.wayfinder.civilization.target.CivilizationNodeTarget;

import java.util.ArrayList;
import java.util.List;

public final class NodeProposalValidator {
    private final double minimumProposalScore;
    private final double duplicateRadius;
    private final double observationRedundancyRadius;

    public NodeProposalValidator(
            double minimumProposalScore,
            double duplicateRadius,
            double observationRedundancyRadius
    ) {
        this.minimumProposalScore = minimumProposalScore;
        this.duplicateRadius = duplicateRadius;
        this.observationRedundancyRadius =
                observationRedundancyRadius;
    }

    public NetworkValidationResult validate(
            NodeProposal proposal,
            NetworkValidationContext context
    ) {
        List<NetworkValidationIssue> issues =
                new ArrayList<>();

        if (proposal.score() < minimumProposalScore) {
            issues.add(
                    new NetworkValidationIssue(
                            NetworkValidationCode.INVALID_PROPOSAL_SCORE,
                            "Proposal score is below the minimum network admission threshold."
                    )
            );
        }

        boolean hasObservationReason =
                hasReason(
                        proposal,
                        ReasonType.OBSERVATION
                );

        if (proposal.purpose()
                == NodePurpose.OBSERVATION
                && !hasObservationReason) {

            issues.add(
                    new NetworkValidationIssue(
                            NetworkValidationCode.INSUFFICIENT_CIVILIZATION_REASON,
                            "Observation proposal lacks an observation reason."
                    )
            );
        }

        boolean hasDirectionReason =
                hasReason(
                        proposal,
                        ReasonType.COMMUNICATION
                )
                || hasReason(
                        proposal,
                        ReasonType.TRAVEL
                )
                || hasReason(
                        proposal,
                        ReasonType.CIVILIZATIONAL
                );

        if (proposal.purpose()
                == NodePurpose.DIRECTION
                && !hasDirectionReason) {

            issues.add(
                    new NetworkValidationIssue(
                            NetworkValidationCode.INSUFFICIENT_CIVILIZATION_REASON,
                            "Direction proposal lacks a communication, travel, or civilization reason."
                    )
            );
        }

        if (proposal.purpose()
                == NodePurpose.DIRECTION
                && !(proposal.target()
                        instanceof CivilizationNodeTarget target)) {

            issues.add(
                    new NetworkValidationIssue(
                            NetworkValidationCode.INSUFFICIENT_CIVILIZATION_REASON,
                            "Direction proposal does not target committed civilization history."
                    )
            );
        } else if (proposal.purpose()
                == NodePurpose.DIRECTION
                && proposal.target()
                        instanceof CivilizationNodeTarget target) {

            boolean targetExists =
                    context.existingNodes()
                            .stream()
                            .anyMatch(existing ->
                                    existing.id()
                                            .equals(
                                                    target.nodeId()
                                            )
                                    && existing.purpose()
                                            == NodePurpose.OBSERVATION
                            );

            if (!targetExists) {
                issues.add(
                        new NetworkValidationIssue(
                                NetworkValidationCode.INSUFFICIENT_CIVILIZATION_REASON,
                                "Direction target is not an existing Observation node."
                        )
                );
            }
        }

        for (CommittedNodeSnapshot existing
                : context.existingNodes()) {

            double distance =
                    horizontalDistance(
                            proposal.position(),
                            existing.position()
                    );

            if (distance < duplicateRadius) {
                issues.add(
                        new NetworkValidationIssue(
                                NetworkValidationCode.DUPLICATE_PROPOSAL_LOCATION,
                                "A committed node already occupies this local site."
                        )
                );
                break;
            }

            if (proposal.purpose()
                    == NodePurpose.OBSERVATION
                    && existing.purpose()
                            == NodePurpose.OBSERVATION
                    && distance
                            < observationRedundancyRadius) {

                issues.add(
                        new NetworkValidationIssue(
                                NetworkValidationCode.OBSERVATION_REDUNDANCY,
                                "An existing observation node already serves this local area."
                        )
                );
                break;
            }
        }

        return issues.isEmpty()
                ? NetworkValidationResult.allow()
                : NetworkValidationResult.reject(
                        issues
                );
    }

    private static boolean hasReason(
            NodeProposal proposal,
            ReasonType type
    ) {
        return proposal.reasons().primary().type()
                        == type
                || proposal.reasons()
                        .supporting()
                        .stream()
                        .anyMatch(reason ->
                                reason.type() == type
                        );
    }

    private static double horizontalDistance(
            com.wayfinder.core.math.WorldPosition a,
            com.wayfinder.core.math.WorldPosition b
    ) {
        double dx = a.x() - b.x();
        double dz = a.z() - b.z();

        return Math.sqrt(
                (dx * dx)
                        + (dz * dz)
        );
    }
}

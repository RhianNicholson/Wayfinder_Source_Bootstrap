package com.wayfinder.civilization.relationship;

import com.wayfinder.civilization.model.NodePurpose;
import com.wayfinder.civilization.reason.ReasonType;
import com.wayfinder.civilization.state.CivilizationNode;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class RelationshipProposalValidator {
    private final double minimumScore;

    public RelationshipProposalValidator(double minimumScore) {
        this.minimumScore = minimumScore;
    }

    public RelationshipValidationResult validate(
            RelationshipProposal proposal,
            RelationshipValidationContext context
    ) {
        List<RelationshipValidationIssue> issues = new ArrayList<>();

        Optional<CivilizationNode> source = context.nodes().stream()
                .filter(node -> node.id().equals(proposal.sourceNodeId()))
                .findFirst();

        Optional<CivilizationNode> target = context.nodes().stream()
                .filter(node -> node.id().equals(proposal.targetNodeId()))
                .findFirst();

        if (source.isEmpty()) {
            issues.add(new RelationshipValidationIssue(
                    RelationshipValidationCode.SOURCE_NODE_MISSING,
                    "The relationship source node is not committed civilization truth."
            ));
        }

        if (target.isEmpty()) {
            issues.add(new RelationshipValidationIssue(
                    RelationshipValidationCode.TARGET_NODE_MISSING,
                    "The relationship target node is not committed civilization truth."
            ));
        }

        if (proposal.sourceNodeId().equals(proposal.targetNodeId())) {
            issues.add(new RelationshipValidationIssue(
                    RelationshipValidationCode.SELF_REFERENCE,
                    "A civilization relationship may not target its own source node."
            ));
        }

        if (proposal.score() < minimumScore) {
            issues.add(new RelationshipValidationIssue(
                    RelationshipValidationCode.SCORE_BELOW_THRESHOLD,
                    "Relationship proposal score is below the admission threshold."
            ));
        }

        boolean hasCivilizationReason =
                proposal.reasons().primary().type() == ReasonType.CIVILIZATIONAL
                || proposal.reasons().primary().type() == ReasonType.COMMUNICATION
                || proposal.reasons().primary().type() == ReasonType.TRAVEL
                || proposal.reasons().supporting().stream()
                        .anyMatch(reason ->
                                reason.type() == ReasonType.CIVILIZATIONAL
                                || reason.type() == ReasonType.COMMUNICATION
                                || reason.type() == ReasonType.TRAVEL
                        );

        if (!hasCivilizationReason) {
            issues.add(new RelationshipValidationIssue(
                    RelationshipValidationCode.INSUFFICIENT_REASON,
                    "Relationship lacks a civilization, communication, or travel reason."
            ));
        }

        if (source.isPresent()
                && target.isPresent()
                && !purposesAreCompatible(
                        proposal.type(),
                        source.get().purpose(),
                        target.get().purpose()
                )) {

            issues.add(new RelationshipValidationIssue(
                    RelationshipValidationCode.INCOMPATIBLE_NODE_PURPOSES,
                    "Node purposes are incompatible with relationship type "
                            + proposal.type()
                            + "."
            ));
        }

        boolean duplicate = context.relationships().stream()
                .anyMatch(existing ->
                        existing.type() == proposal.type()
                                && existing.sourceNodeId()
                                        .equals(proposal.sourceNodeId())
                                && existing.targetNodeId()
                                        .equals(proposal.targetNodeId())
                );

        if (duplicate) {
            issues.add(new RelationshipValidationIssue(
                    RelationshipValidationCode.DUPLICATE_RELATIONSHIP,
                    "An equivalent committed relationship already exists."
            ));
        }

        return issues.isEmpty()
                ? RelationshipValidationResult.allow()
                : RelationshipValidationResult.reject(issues);
    }

    private boolean purposesAreCompatible(
            RelationshipType type,
            NodePurpose source,
            NodePurpose target
    ) {
        return switch (type) {
            case DIRECTIONAL_REFERENCE ->
                    source == NodePurpose.DIRECTION
                            && target == NodePurpose.OBSERVATION;

            case OBSERVES ->
                    source == NodePurpose.OBSERVATION
                            && target != NodePurpose.OBSERVATION;

            case ROUTE ->
                    source != NodePurpose.RECORD
                            && source != NodePurpose.PRESERVATION
                            && target != NodePurpose.RECORD
                            && target != NodePurpose.PRESERVATION;
        };
    }
}

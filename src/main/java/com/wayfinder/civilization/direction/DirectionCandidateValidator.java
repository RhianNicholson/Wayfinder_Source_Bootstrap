package com.wayfinder.civilization.direction;

import com.wayfinder.civilization.model.NodePurpose;
import com.wayfinder.civilization.state.CivilizationNode;

import java.util.List;

/**
 * Hard validity checks. Invalid candidates do not receive a low score; they
 * cease to be possibilities.
 */
public final class DirectionCandidateValidator {
    private final double minimumBuildability;
    private final double minimumDistance;
    private final double maximumDistance;
    private final double existingNodeExclusionRadius;

    public DirectionCandidateValidator(
            double minimumBuildability,
            double minimumDistance,
            double maximumDistance,
            double existingNodeExclusionRadius
    ) {
        this.minimumBuildability = minimumBuildability;
        this.minimumDistance = minimumDistance;
        this.maximumDistance = maximumDistance;
        this.existingNodeExclusionRadius = existingNodeExclusionRadius;
    }

    public boolean isValid(
            DirectionCandidate candidate,
            List<CivilizationNode> committedNodes
    ) {
        if (candidate.destination().purpose()
                != NodePurpose.OBSERVATION) {
            return false;
        }

        if (candidate.buildability() < minimumBuildability) {
            return false;
        }

        if (candidate.distance() < minimumDistance
                || candidate.distance() > maximumDistance) {
            return false;
        }

        /*
         * The destination itself is intentionally excluded from this
         * redundancy check. It is the reason this candidate exists.
         */
        return committedNodes.stream()
                .filter(node ->
                        !node.id().equals(
                                candidate.destination().id()
                        )
                )
                .noneMatch(node ->
                        node.position()
                                .horizontalDistanceTo(
                                        candidate.position()
                                )
                                < existingNodeExclusionRadius
                );
    }
}

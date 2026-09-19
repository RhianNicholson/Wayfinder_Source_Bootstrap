package com.wayfinder.history;

import com.wayfinder.core.id.NodeId;

import java.util.Optional;

/**
 * Describes whether committed history still requires a physical consequence.
 */
public record HistoricalPhysicalizationResult(
        Optional<NodeId> targetNodeId,
        boolean requiresPhysicalization
) {
    public static HistoricalPhysicalizationResult none() {
        return new HistoricalPhysicalizationResult(Optional.empty(), false);
    }

    public static HistoricalPhysicalizationResult required(NodeId nodeId) {
        return new HistoricalPhysicalizationResult(Optional.of(nodeId), true);
    }
}

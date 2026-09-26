package com.wayfinder.history;

import com.wayfinder.core.id.NodeId;

/**
 * A plausible historical event that has already passed event-specific
 * eligibility checks. Opportunities describe possibilities; they do not
 * mutate state and they do not imply that an event must occur.
 */
public record HistoricalEventOpportunity(
        HistoricalEventType type,
        NodeId affectedNodeId,
        double weight,
        String reason
) {
    public HistoricalEventOpportunity {
        if (type == null) throw new IllegalArgumentException("type is required");
        if (affectedNodeId == null) throw new IllegalArgumentException("affectedNodeId is required");
        if (weight <= 0.0) throw new IllegalArgumentException("weight must be positive");
        if (reason == null || reason.isBlank()) throw new IllegalArgumentException("reason is required");
    }
}

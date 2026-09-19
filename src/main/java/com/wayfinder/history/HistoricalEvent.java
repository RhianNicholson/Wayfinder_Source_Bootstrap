package com.wayfinder.history;

import com.wayfinder.core.id.NodeId;

import java.util.UUID;

public record HistoricalEvent(
        UUID id,
        long sequence,
        HistoricalEventType type,
        NodeId affectedNodeId,
        String cause
) {
    public HistoricalEvent {
        if (id == null || type == null || affectedNodeId == null) {
            throw new IllegalArgumentException("historical event identity is required");
        }
        if (sequence < 1) {
            throw new IllegalArgumentException("historical event sequence must be positive");
        }
        if (cause == null || cause.isBlank()) {
            throw new IllegalArgumentException("historical event cause is required");
        }
    }
}

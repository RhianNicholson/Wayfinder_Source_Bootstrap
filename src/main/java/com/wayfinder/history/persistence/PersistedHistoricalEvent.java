package com.wayfinder.history.persistence;

public record PersistedHistoricalEvent(
        String id,
        long sequence,
        String type,
        String affectedNodeId,
        String cause
) {}

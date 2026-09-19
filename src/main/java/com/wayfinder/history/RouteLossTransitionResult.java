package com.wayfinder.history;

import com.wayfinder.structure.materialization.MaterializationState;

public record RouteLossTransitionResult(
        boolean applied,
        HistoricalEventState historicalState,
        MaterializationState materializationState,
        HistoricalEvent event,
        int removedBlocks,
        String stage
) {}

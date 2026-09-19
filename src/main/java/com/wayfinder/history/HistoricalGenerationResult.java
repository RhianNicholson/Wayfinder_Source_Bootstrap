package com.wayfinder.history;

import java.util.Optional;

/** Result of one historical-generation evaluation. */
public record HistoricalGenerationResult(
        HistoricalEventState state,
        RouteLossDecision decision,
        Optional<HistoricalEvent> committedEvent
) {
    public HistoricalGenerationResult {
        committedEvent = committedEvent == null ? Optional.empty() : committedEvent;
    }
}

package com.wayfinder.history;

import java.util.Optional;

public record GeneralizedHistoricalGenerationResult(
        HistoricalEventState state,
        HistoricalOpportunityDecision selectionDecision,
        Optional<HistoricalEventOccurrenceDecision> occurrenceDecision,
        Optional<HistoricalEvent> committedEvent
) {}

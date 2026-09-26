package com.wayfinder.history;

import java.util.Optional;

public record HistoricalOpportunityDecision(
        Optional<HistoricalEventOpportunity> opportunity,
        double selectionRoll
) {
    public static HistoricalOpportunityDecision none() {
        return new HistoricalOpportunityDecision(Optional.empty(), 1.0);
    }
}

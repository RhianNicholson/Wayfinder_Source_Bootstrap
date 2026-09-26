package com.wayfinder.history;

/**
 * Event-specific occurrence decision after a plausible opportunity has been
 * selected. Selection answers "which possibility?"; this answers "did it
 * actually happen?"
 */
public interface HistoricalEventOccurrenceDecider {
    HistoricalEventType type();

    HistoricalEventOccurrenceDecision decide(
            long worldSeed,
            HistoricalScope scope,
            HistoricalEventOpportunity opportunity
    );
}

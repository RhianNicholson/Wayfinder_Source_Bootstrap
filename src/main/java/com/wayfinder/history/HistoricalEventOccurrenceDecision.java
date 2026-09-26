package com.wayfinder.history;

public record HistoricalEventOccurrenceDecision(
        boolean occurs,
        double roll,
        double threshold
) {
    public static HistoricalEventOccurrenceDecision noOccurrence(
            double roll,
            double threshold
    ) {
        return new HistoricalEventOccurrenceDecision(false, roll, threshold);
    }
}

package com.wayfinder.history;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

final class HistoricalEventOpportunityTest {
    @Test
    void opportunityRequiresPositiveWeight() {
        assertThrows(IllegalArgumentException.class, () ->
                new HistoricalEventOpportunity(
                        HistoricalEventType.ROUTE_LOSS,
                        null,
                        0.0,
                        "invalid"));
    }
}

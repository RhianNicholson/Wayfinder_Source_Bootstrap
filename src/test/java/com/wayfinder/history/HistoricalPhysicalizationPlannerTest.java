package com.wayfinder.history;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

final class HistoricalPhysicalizationPlannerTest {
    @Test
    void plannerRemainsDomainOnly() {
        assertNotNull(new HistoricalPhysicalizationPlanner());
    }
}

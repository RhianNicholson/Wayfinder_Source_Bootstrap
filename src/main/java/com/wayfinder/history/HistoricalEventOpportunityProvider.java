package com.wayfinder.history;

import com.wayfinder.civilization.state.CivilizationState;
import com.wayfinder.structure.materialization.MaterializationState;

import java.util.List;

/**
 * Event-specific systems expose plausible historical opportunities through
 * this boundary. Randomness may later choose among them, but may not create
 * opportunities that were not justified here.
 */
public interface HistoricalEventOpportunityProvider {
    List<HistoricalEventOpportunity> opportunities(
            HistoricalScope scope,
            CivilizationState civilization,
            MaterializationState materialization,
            HistoricalEventState history
    );
}

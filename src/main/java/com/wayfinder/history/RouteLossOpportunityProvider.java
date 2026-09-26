package com.wayfinder.history;

import com.wayfinder.civilization.state.CivilizationState;
import com.wayfinder.structure.materialization.MaterializationState;

import java.util.List;

/**
 * Adapter exposing the existing, validated ROUTE_LOSS candidate logic through
 * the generic historical-opportunity boundary.
 */
public final class RouteLossOpportunityProvider
        implements HistoricalEventOpportunityProvider {

    private final ProceduralRouteLossSelector selector;

    public RouteLossOpportunityProvider(ProceduralRouteLossSelector selector) {
        this.selector = selector;
    }

    @Override
    public List<HistoricalEventOpportunity> opportunities(
            HistoricalScope scope,
            CivilizationState civilization,
            MaterializationState materialization,
            HistoricalEventState history
    ) {
        return selector.candidates(
                        civilization,
                        materialization,
                        history,
                        scope.region())
                .stream()
                .map(candidate -> new HistoricalEventOpportunity(
                        HistoricalEventType.ROUTE_LOSS,
                        candidate.destinationNodeId(),
                        candidate.vulnerability(),
                        candidate.reason()))
                .toList();
    }
}

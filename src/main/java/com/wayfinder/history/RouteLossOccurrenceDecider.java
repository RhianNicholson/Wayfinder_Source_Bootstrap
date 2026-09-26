package com.wayfinder.history;

import com.wayfinder.core.random.RandomStreamFactory;
import com.wayfinder.core.random.RandomStreamKey;

/**
 * Preserves ROUTE_LOSS occurrence semantics in the generalized pipeline.
 *
 * The opportunity weight is the route-loss vulnerability inherited from the
 * validated RouteLossCandidate model. It is used as the occurrence threshold.
 */
public final class RouteLossOccurrenceDecider
        implements HistoricalEventOccurrenceDecider {

    private final RandomStreamFactory randomStreams;

    public RouteLossOccurrenceDecider(RandomStreamFactory randomStreams) {
        this.randomStreams = randomStreams;
    }

    @Override
    public HistoricalEventType type() {
        return HistoricalEventType.ROUTE_LOSS;
    }

    @Override
    public HistoricalEventOccurrenceDecision decide(
            long worldSeed,
            HistoricalScope scope,
            HistoricalEventOpportunity opportunity
    ) {
        if (opportunity.type() != HistoricalEventType.ROUTE_LOSS)
            return HistoricalEventOccurrenceDecision.noOccurrence(1.0, 0.0);

        double threshold = opportunity.weight();

        var random = randomStreams.create(new RandomStreamKey(
                worldSeed,
                scope.regionKey(),
                "historical-route-loss-occurrence",
                scope.eraKey(),
                scope.generationVersion()));

        double roll = random.nextDouble();
        return new HistoricalEventOccurrenceDecision(
                roll < threshold,
                roll,
                threshold);
    }
}

package com.wayfinder.history;

import com.wayfinder.core.random.RandomStreamFactory;
import com.wayfinder.core.random.RandomStreamKey;

import java.util.Comparator;
import java.util.List;

/**
 * Deterministically chooses among already-justified historical opportunities.
 * It never creates opportunities.
 */
public final class DeterministicHistoricalOpportunitySelector {
    private final RandomStreamFactory randomStreams;

    public DeterministicHistoricalOpportunitySelector(RandomStreamFactory randomStreams) {
        this.randomStreams = randomStreams;
    }

    public HistoricalOpportunityDecision select(
            long worldSeed,
            HistoricalScope scope,
            List<HistoricalEventOpportunity> opportunities
    ) {
        if (opportunities.isEmpty())
            return HistoricalOpportunityDecision.none();

        var ordered = opportunities.stream()
                .sorted(Comparator
                        .comparing((HistoricalEventOpportunity o) -> o.type().name())
                        .thenComparing(o -> o.affectedNodeId().value().toString())
                        .thenComparing(HistoricalEventOpportunity::reason))
                .toList();

        double totalWeight = ordered.stream()
                .mapToDouble(HistoricalEventOpportunity::weight)
                .sum();

        var random = randomStreams.create(new RandomStreamKey(
                worldSeed,
                scope.regionKey(),
                "historical-opportunity-selection",
                scope.eraKey(),
                scope.generationVersion()));

        double roll = random.nextDouble();
        double cursor = roll * totalWeight;

        for (var opportunity : ordered) {
            cursor -= opportunity.weight();
            if (cursor < 0.0)
                return new HistoricalOpportunityDecision(
                        java.util.Optional.of(opportunity), roll);
        }

        return new HistoricalOpportunityDecision(
                java.util.Optional.of(ordered.get(ordered.size() - 1)), roll);
    }
}

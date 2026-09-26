package com.wayfinder.history;

import com.wayfinder.civilization.state.CivilizationState;
import com.wayfinder.structure.materialization.MaterializationState;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * General historical generation pipeline:
 *
 * possibilities -> deterministic selection -> event-specific occurrence
 * -> event-specific validation/commit.
 */
public final class GeneralizedHistoricalGenerationService {
    private final List<HistoricalEventOpportunityProvider> providers;
    private final DeterministicHistoricalOpportunitySelector selector;
    private final HistoricalEventOccurrenceRegistry occurrence;
    private final HistoricalEventHandlerRegistry handlers;

    public GeneralizedHistoricalGenerationService(
            List<HistoricalEventOpportunityProvider> providers,
            DeterministicHistoricalOpportunitySelector selector,
            HistoricalEventOccurrenceRegistry occurrence,
            HistoricalEventHandlerRegistry handlers
    ) {
        this.providers = List.copyOf(providers);
        this.selector = selector;
        this.occurrence = occurrence;
        this.handlers = handlers;
    }

    public GeneralizedHistoricalGenerationResult generate(
            long worldSeed,
            HistoricalScope scope,
            CivilizationState civilization,
            MaterializationState materialization,
            HistoricalEventState history
    ) {
        var opportunities = providers.stream()
                .flatMap(provider -> provider.opportunities(
                        scope, civilization, materialization, history).stream())
                .sorted(Comparator
                        .comparing((HistoricalEventOpportunity o) -> o.type().name())
                        .thenComparing(o -> o.affectedNodeId().value().toString())
                        .thenComparing(HistoricalEventOpportunity::reason))
                .toList();

        var selection = selector.select(worldSeed, scope, opportunities);

        if (selection.opportunity().isEmpty()) {
            return new GeneralizedHistoricalGenerationResult(
                    history, selection, Optional.empty(), Optional.empty());
        }

        var opportunity = selection.opportunity().orElseThrow();
        var decider = occurrence.deciderFor(opportunity.type());

        if (decider.isEmpty()) {
            return new GeneralizedHistoricalGenerationResult(
                    history, selection, Optional.empty(), Optional.empty());
        }

        var occurrenceDecision = decider.orElseThrow()
                .decide(worldSeed, scope, opportunity);

        if (!occurrenceDecision.occurs()) {
            return new GeneralizedHistoricalGenerationResult(
                    history,
                    selection,
                    Optional.of(occurrenceDecision),
                    Optional.empty());
        }

        var handler = handlers.handlerFor(opportunity.type());
        if (handler.isEmpty()) {
            return new GeneralizedHistoricalGenerationResult(
                    history,
                    selection,
                    Optional.of(occurrenceDecision),
                    Optional.empty());
        }

        var handled = handler.orElseThrow().handle(
                opportunity, scope, materialization, history);

        return new GeneralizedHistoricalGenerationResult(
                handled.state(),
                selection,
                Optional.of(occurrenceDecision),
                handled.committedEvent());
    }
}

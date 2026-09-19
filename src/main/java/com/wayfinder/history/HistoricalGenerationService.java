package com.wayfinder.history;

import com.wayfinder.civilization.state.CivilizationState;
import com.wayfinder.structure.materialization.MaterializationState;

import java.util.Optional;

public final class HistoricalGenerationService {
    private final ProceduralRouteLossSelector selector;
    private final DeterministicRouteLossDecisionService decisions;
    private final HistoricalEventFactory events;
    private final HistoricalEventCommitService commits;
    private final RouteLossTransitionService validation;

    public HistoricalGenerationService(
            ProceduralRouteLossSelector selector,
            DeterministicRouteLossDecisionService decisions,
            HistoricalEventFactory events,
            HistoricalEventCommitService commits,
            RouteLossTransitionService validation
    ) {
        this.selector = selector;
        this.decisions = decisions;
        this.events = events;
        this.commits = commits;
        this.validation = validation;
    }

    public HistoricalGenerationResult generate(
            long worldSeed,
            HistoricalScope scope,
            CivilizationState civilization,
            MaterializationState materialization,
            HistoricalEventState history
    ) {
        var candidates = selector.candidates(
                civilization, materialization, history, scope.region());

        var decision = decisions.decide(
                worldSeed,
                scope.regionKey(),
                scope.eraKey(),
                scope.generationVersion(),
                candidates);

        if (!decision.occurs() || decision.candidate().isEmpty())
            return new HistoricalGenerationResult(history, decision, Optional.empty());

        var candidate = decision.candidate().orElseThrow();
        var validationResult = validation.validate(
                history, materialization, candidate.destinationNodeId());

        if (!validationResult.valid())
            return new HistoricalGenerationResult(history, decision, Optional.empty());

        var event = events.routeLoss(
                history,
                candidate.destinationNodeId(),
                "procedural route loss [" + scope.eraKey() + "]");

        var committed = commits.commit(history, event);
        if (!committed.committed())
            return new HistoricalGenerationResult(history, decision, Optional.empty());

        return new HistoricalGenerationResult(
                committed.state(), decision, Optional.of(event));
    }

    /** Compatibility bridge for pre-M41 callers. */
    public HistoricalGenerationResult generate(
            long worldSeed,
            String regionKey,
            String eraKey,
            int generationVersion,
            CivilizationState civilization,
            MaterializationState materialization,
            HistoricalEventState history
    ) {
        var candidates = selector.candidates(civilization, materialization, history);
        var decision = decisions.decide(
                worldSeed, regionKey, eraKey, generationVersion, candidates);

        if (!decision.occurs() || decision.candidate().isEmpty())
            return new HistoricalGenerationResult(history, decision, Optional.empty());

        var candidate = decision.candidate().orElseThrow();
        var validationResult = validation.validate(
                history, materialization, candidate.destinationNodeId());
        if (!validationResult.valid())
            return new HistoricalGenerationResult(history, decision, Optional.empty());

        var event = events.routeLoss(
                history, candidate.destinationNodeId(),
                "procedural route loss [" + eraKey + "]");
        var committed = commits.commit(history, event);
        if (!committed.committed())
            return new HistoricalGenerationResult(history, decision, Optional.empty());

        return new HistoricalGenerationResult(
                committed.state(), decision, Optional.of(event));
    }
}

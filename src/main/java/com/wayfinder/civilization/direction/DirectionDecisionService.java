package com.wayfinder.civilization.direction;

import com.wayfinder.civilization.model.NodePurpose;
import com.wayfinder.civilization.reason.ReasonEvaluation;
import com.wayfinder.civilization.state.CivilizationNode;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class DirectionDecisionService {
    private final DirectionCandidateGenerator generator;
    private final DirectionCandidateValidator validator;
    private final DirectionReasonEvaluator reasonEvaluator;
    private final DirectionCandidateScorer scorer;
    private final DirectionPlausibilityFilter plausibilityFilter;
    private final DeterministicDirectionSelector selector;

    public DirectionDecisionService(
            DirectionCandidateGenerator generator,
            DirectionCandidateValidator validator,
            DirectionReasonEvaluator reasonEvaluator,
            DirectionCandidateScorer scorer,
            DirectionPlausibilityFilter plausibilityFilter,
            DeterministicDirectionSelector selector
    ) {
        this.generator = generator;
        this.validator = validator;
        this.reasonEvaluator = reasonEvaluator;
        this.scorer = scorer;
        this.plausibilityFilter = plausibilityFilter;
        this.selector = selector;
    }

    public Optional<DirectionDecision> decide(
            CivilizationNode destination,
            List<CivilizationNode> committedNodes
    ) {
        List<DirectionCandidate> generated =
                generator.generate(destination);

        List<DirectionCandidate> valid =
                generated.stream()
                        .filter(candidate ->
                                validator.isValid(
                                        candidate,
                                        committedNodes
                                )
                        )
                        .toList();

        List<ScoredDirectionCandidate> scored =
                new ArrayList<>();

        for (DirectionCandidate candidate : valid) {
            ReasonEvaluation reasons =
                    reasonEvaluator.evaluate(candidate);

            scored.add(
                    scorer.score(
                            candidate,
                            reasons
                    )
            );
        }

        List<ScoredDirectionCandidate> plausible =
                plausibilityFilter.eligible(scored);

        return selector.select(plausible)
                .map(selected ->
                        new DirectionDecision(
                                NodePurpose.DIRECTION,
                                selected,
                                generated.size(),
                                valid.size(),
                                plausible.size()
                        )
                );
    }
}

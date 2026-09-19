package com.wayfinder.civilization.service;

import com.wayfinder.civilization.candidate.ObservationCandidate;
import com.wayfinder.civilization.candidate.ObservationCandidateGenerator;
import com.wayfinder.civilization.candidate.ObservationCandidateValidator;
import com.wayfinder.civilization.model.NodePurpose;
import com.wayfinder.civilization.reason.ObservationReasonEvaluator;
import com.wayfinder.civilization.scoring.ObservationCandidateScorer;
import com.wayfinder.civilization.scoring.ScoredObservationCandidate;
import com.wayfinder.civilization.selection.DeterministicObservationSelector;
import com.wayfinder.civilization.selection.ObservationPlausibilityFilter;
import com.wayfinder.geography.model.Landmark;
import com.wayfinder.geography.model.ObservationSite;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class ObservationDecisionService {
    private final ObservationCandidateGenerator generator;
    private final ObservationCandidateValidator validator;
    private final ObservationReasonEvaluator reasonEvaluator;
    private final ObservationCandidateScorer scorer;
    private final ObservationPlausibilityFilter plausibilityFilter;
    private final DeterministicObservationSelector selector;

    public ObservationDecisionService(
            ObservationCandidateGenerator generator,
            ObservationCandidateValidator validator,
            ObservationReasonEvaluator reasonEvaluator,
            ObservationCandidateScorer scorer,
            ObservationPlausibilityFilter plausibilityFilter,
            DeterministicObservationSelector selector
    ) {
        this.generator = generator;
        this.validator = validator;
        this.reasonEvaluator = reasonEvaluator;
        this.scorer = scorer;
        this.plausibilityFilter = plausibilityFilter;
        this.selector = selector;
    }

    public Optional<ObservationDecision> decide(
            List<ObservationSite> sites,
            Landmark target
    ) {
        List<ObservationCandidate> generated = generator.generate(sites, target);

        List<ObservationCandidate> valid = generated.stream()
                .filter(candidate -> validator.validate(candidate).valid())
                .toList();

        List<ScoredObservationCandidate> scored = new ArrayList<>();
        for (ObservationCandidate candidate : valid) {
            var reasons = reasonEvaluator.evaluate(candidate);
            scored.add(scorer.score(candidate, reasons));
        }

        List<ScoredObservationCandidate> plausible = plausibilityFilter.eligible(scored);

        return selector.select(plausible)
                .map(selected -> new ObservationDecision(
                        NodePurpose.OBSERVATION,
                        selected,
                        generated.size(),
                        valid.size(),
                        plausible.size()
                ));
    }
}

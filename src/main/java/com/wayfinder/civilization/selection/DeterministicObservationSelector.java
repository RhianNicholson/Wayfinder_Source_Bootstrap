package com.wayfinder.civilization.selection;

import com.wayfinder.civilization.scoring.ScoredObservationCandidate;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * First civilization selector.
 *
 * v1 deliberately selects the highest-scoring eligible candidate with a stable
 * candidate-key tie break. The interface is intentionally isolated so this can
 * be replaced by the scoped weighted-random selector without touching validity,
 * reasons, scoring, or network code.
 */
public final class DeterministicObservationSelector {

    public Optional<ScoredObservationCandidate> select(
            List<ScoredObservationCandidate> eligible
    ) {
        return eligible.stream()
                .sorted(
                        Comparator.comparingDouble(
                                (ScoredObservationCandidate candidate) ->
                                        candidate.score().finalScore()
                        ).reversed()
                        .thenComparing(candidate -> candidate.candidate().candidateKey())
                )
                .findFirst();
    }
}

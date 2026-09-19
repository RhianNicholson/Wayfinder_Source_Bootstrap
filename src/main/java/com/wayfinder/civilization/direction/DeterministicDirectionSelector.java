package com.wayfinder.civilization.direction;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * Temporary deterministic selector.
 *
 * As with Observation selection, randomness will later choose only among
 * already plausible candidates. Randomness never creates possibilities.
 */
public final class DeterministicDirectionSelector {

    public Optional<ScoredDirectionCandidate> select(
            List<ScoredDirectionCandidate> eligible
    ) {
        return eligible.stream()
                .sorted(
                        Comparator.comparingDouble(
                                (ScoredDirectionCandidate candidate) ->
                                        candidate.score()
                                                .finalScore()
                        ).reversed()
                                .thenComparing(
                                        candidate ->
                                                candidate.candidate()
                                                        .candidateKey()
                                )
                )
                .findFirst();
    }
}

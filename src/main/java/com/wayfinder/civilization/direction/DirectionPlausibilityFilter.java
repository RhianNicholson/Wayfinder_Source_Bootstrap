package com.wayfinder.civilization.direction;

import java.util.Comparator;
import java.util.List;

public final class DirectionPlausibilityFilter {
    private final double absoluteMinimum;
    private final double relativeThreshold;

    public DirectionPlausibilityFilter(
            double absoluteMinimum,
            double relativeThreshold
    ) {
        this.absoluteMinimum = absoluteMinimum;
        this.relativeThreshold = relativeThreshold;
    }

    public List<ScoredDirectionCandidate> eligible(
            List<ScoredDirectionCandidate> candidates
    ) {
        if (candidates.isEmpty()) {
            return List.of();
        }

        double best = candidates.stream()
                .mapToDouble(
                        candidate ->
                                candidate.score().finalScore()
                )
                .max()
                .orElse(0.0);

        double relativeMinimum =
                best * relativeThreshold;

        return candidates.stream()
                .filter(candidate ->
                        candidate.score().finalScore()
                                >= absoluteMinimum
                )
                .filter(candidate ->
                        candidate.score().finalScore()
                                >= relativeMinimum
                )
                .sorted(
                        Comparator.comparingDouble(
                                (ScoredDirectionCandidate candidate) ->
                                        candidate.score()
                                                .finalScore()
                        ).reversed()
                )
                .toList();
    }
}

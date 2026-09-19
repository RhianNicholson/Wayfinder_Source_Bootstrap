package com.wayfinder.civilization.selection;

import com.wayfinder.civilization.scoring.ScoredObservationCandidate;

import java.util.Comparator;
import java.util.List;

public final class ObservationPlausibilityFilter {
    private final double absoluteMinimum;
    private final double relativeThreshold;

    public ObservationPlausibilityFilter(double absoluteMinimum, double relativeThreshold) {
        this.absoluteMinimum = absoluteMinimum;
        this.relativeThreshold = relativeThreshold;
    }

    public List<ScoredObservationCandidate> eligible(List<ScoredObservationCandidate> candidates) {
        if (candidates.isEmpty()) return List.of();

        double best = candidates.stream()
                .mapToDouble(candidate -> candidate.score().finalScore())
                .max()
                .orElse(0.0);

        double relativeMinimum = best * relativeThreshold;

        return candidates.stream()
                .filter(candidate -> candidate.score().finalScore() >= absoluteMinimum)
                .filter(candidate -> candidate.score().finalScore() >= relativeMinimum)
                .sorted(Comparator.comparingDouble(
                        (ScoredObservationCandidate candidate) -> candidate.score().finalScore()
                ).reversed())
                .toList();
    }
}

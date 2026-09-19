package com.wayfinder.civilization.direction;

import com.wayfinder.civilization.reason.ReasonEvaluation;

public final class DirectionCandidateScorer {

    public ScoredDirectionCandidate score(
            DirectionCandidate candidate,
            ReasonEvaluation reasons
    ) {
        double finalScore =
                candidate.directionalClarity() * 0.30
                        + candidate.travelFit() * 0.25
                        + candidate.buildability() * 0.20
                        + candidate.destination().score() * 0.25;

        return new ScoredDirectionCandidate(
                candidate,
                new DirectionCandidateScore(
                        clamp(finalScore),
                        candidate.directionalClarity(),
                        candidate.travelFit(),
                        candidate.buildability(),
                        candidate.destination().score()
                ),
                reasons
        );
    }

    private static double clamp(double value) {
        return Math.max(0.0, Math.min(1.0, value));
    }
}

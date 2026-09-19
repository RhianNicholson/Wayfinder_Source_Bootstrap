package com.wayfinder.civilization.scoring;

import com.wayfinder.civilization.candidate.ObservationCandidate;
import com.wayfinder.civilization.reason.ReasonEvaluation;

public final class ObservationCandidateScorer {

    public ScoredObservationCandidate score(
            ObservationCandidate candidate,
            ReasonEvaluation reasons
    ) {
        double visibility = candidate.sightline().score();
        double salience = candidate.target().salience();
        double buildability = candidate.site().buildability();

        /*
         * ObservationSite does not expose a separate terrainFit value.
         * Its existing coarse site score already combines the geographic
         * suitability factors calculated by the semantic-geography layer.
         *
         * Civilization therefore consumes that lower-layer fact instead of
         * inventing a new field or reaching back into terrain analysis.
         */
        double terrainFit = candidate.site().score();

        double finalScore =
                (visibility * 0.40)
                + (salience * 0.25)
                + (buildability * 0.20)
                + (terrainFit * 0.15);

        return new ScoredObservationCandidate(
                candidate,
                reasons,
                new ObservationCandidateScore(
                        visibility,
                        salience,
                        buildability,
                        terrainFit,
                        clamp(finalScore)
                )
        );
    }

    private static double clamp(double value) {
        return Math.max(0.0, Math.min(1.0, value));
    }
}

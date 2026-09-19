package com.wayfinder.civilization.reason;

import com.wayfinder.civilization.candidate.ObservationCandidate;

import java.util.List;

public final class ObservationReasonEvaluator {

    public ReasonEvaluation evaluate(ObservationCandidate candidate) {
        double observationContribution =
                clamp((candidate.sightline().score() * 0.65) + (candidate.target().salience() * 0.35));

        ReasonRecord primary = new ReasonRecord(
                ReasonType.OBSERVATION,
                observationContribution,
                "The site provides a useful sightline to a regionally meaningful landmark."
        );

        ReasonRecord geographic = new ReasonRecord(
                ReasonType.GEOGRAPHIC,
                clamp((candidate.site().buildability() * 0.45)
                        + (candidate.target().prominence() / 32.0 * 0.55)),
                "Terrain creates a defensible observation opportunity."
        );

        ReasonRecord communication = new ReasonRecord(
                ReasonType.COMMUNICATION,
                clamp(candidate.target().salience()),
                "The landmark is distinct enough to participate in a navigational relationship."
        );

        return new ReasonEvaluation(primary, List.of(geographic, communication));
    }

    private static double clamp(double value) {
        return Math.max(0.0, Math.min(1.0, value));
    }
}

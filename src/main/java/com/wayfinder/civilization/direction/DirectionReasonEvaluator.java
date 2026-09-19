package com.wayfinder.civilization.direction;

import com.wayfinder.civilization.reason.ReasonEvaluation;
import com.wayfinder.civilization.reason.ReasonRecord;
import com.wayfinder.civilization.reason.ReasonType;

import java.util.List;

/**
 * Explains why a valid direction candidate matters to the civilization.
 */
public final class DirectionReasonEvaluator {

    public ReasonEvaluation evaluate(
            DirectionCandidate candidate
    ) {
        double communication =
                clamp(
                        candidate.directionalClarity() * 0.60
                                + candidate.destination().score()
                                * 0.40
                );

        double travel =
                clamp(
                        candidate.travelFit() * 0.70
                                + candidate.buildability()
                                * 0.30
                );

        return new ReasonEvaluation(
                new ReasonRecord(
                        ReasonType.COMMUNICATION,
                        communication,
                        "This location can deliberately communicate the route toward a committed observation site."
                ),
                List.of(
                        new ReasonRecord(
                                ReasonType.TRAVEL,
                                travel,
                                "The approach is geographically plausible for a traveler moving toward the observation site."
                        ),
                        new ReasonRecord(
                                ReasonType.CIVILIZATIONAL,
                                candidate.destination().score(),
                                "The destination is already committed Wayfinder history, so this node extends an existing purpose rather than inventing one."
                        )
                )
        );
    }

    private static double clamp(double value) {
        return Math.max(0.0, Math.min(1.0, value));
    }
}

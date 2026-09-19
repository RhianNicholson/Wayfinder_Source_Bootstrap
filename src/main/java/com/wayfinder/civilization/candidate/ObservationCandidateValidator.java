package com.wayfinder.civilization.candidate;

import com.wayfinder.core.validation.ValidationIssue;
import com.wayfinder.core.validation.ValidationResult;
import com.wayfinder.core.validation.ValidationSeverity;

import java.util.ArrayList;
import java.util.List;

public final class ObservationCandidateValidator {
    private final double minimumSightlineScore;
    private final double minimumBuildability;

    public ObservationCandidateValidator(double minimumSightlineScore, double minimumBuildability) {
        this.minimumSightlineScore = minimumSightlineScore;
        this.minimumBuildability = minimumBuildability;
    }

    public ValidationResult validate(ObservationCandidate candidate) {
        List<ValidationIssue> issues = new ArrayList<>();

        if (!candidate.sightline().visible() || candidate.sightline().score() < minimumSightlineScore) {
            issues.add(new ValidationIssue(
                    ValidationSeverity.ERROR,
                    "OBSERVATION_SIGHTLINE_INVALID",
                    "Candidate does not have a sufficient sightline to its target."
            ));
        }

        if (candidate.site().buildability() < minimumBuildability) {
            issues.add(new ValidationIssue(
                    ValidationSeverity.ERROR,
                    "OBSERVATION_SITE_UNBUILDABLE",
                    "Candidate terrain is not sufficiently buildable."
            ));
        }

        if (candidate.target().salience() <= 0.0) {
            issues.add(new ValidationIssue(
                    ValidationSeverity.ERROR,
                    "OBSERVATION_TARGET_MEANINGLESS",
                    "Candidate target has no landmark salience."
            ));
        }

        return new ValidationResult(issues);
    }
}

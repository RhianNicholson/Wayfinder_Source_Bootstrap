package com.wayfinder.civilization.scoring;

import com.wayfinder.civilization.candidate.ObservationCandidate;
import com.wayfinder.civilization.reason.ReasonEvaluation;

public record ScoredObservationCandidate(
        ObservationCandidate candidate,
        ReasonEvaluation reasons,
        ObservationCandidateScore score
) {}

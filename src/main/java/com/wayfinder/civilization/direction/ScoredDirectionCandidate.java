package com.wayfinder.civilization.direction;

import com.wayfinder.civilization.reason.ReasonEvaluation;

public record ScoredDirectionCandidate(
        DirectionCandidate candidate,
        DirectionCandidateScore score,
        ReasonEvaluation reasons
) {}

package com.wayfinder.civilization.service;

import com.wayfinder.civilization.model.NodePurpose;
import com.wayfinder.civilization.scoring.ScoredObservationCandidate;

public record ObservationDecision(
        NodePurpose purpose,
        ScoredObservationCandidate selected,
        int generatedCandidates,
        int validCandidates,
        int plausibleCandidates
) {}

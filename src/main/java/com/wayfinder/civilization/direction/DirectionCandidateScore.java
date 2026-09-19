package com.wayfinder.civilization.direction;

public record DirectionCandidateScore(
        double finalScore,
        double directionalClarity,
        double travelFit,
        double buildability,
        double destinationImportance
) {}

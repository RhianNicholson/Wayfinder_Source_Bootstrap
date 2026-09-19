package com.wayfinder.civilization.scoring;

public record ObservationCandidateScore(
        double visibility,
        double landmarkSalience,
        double buildability,
        double terrainFit,
        double finalScore
) {}

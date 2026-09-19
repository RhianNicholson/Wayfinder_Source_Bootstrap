package com.wayfinder.geography.analysis;

import com.wayfinder.geography.model.TerrainMetrics;
import com.wayfinder.geography.model.TerrainProfile;

public final class TerrainProfileClassifier {
    public TerrainProfile classify(TerrainMetrics metrics) {
        if (metrics.elevationVariance() < 4.0 && metrics.ruggedness() < 0.08) return TerrainProfile.FLAT;
        if (metrics.elevationVariance() < 40.0 && metrics.ruggedness() < 0.20) return TerrainProfile.ROLLING;
        if (metrics.ruggedness() >= 0.55) return TerrainProfile.RUGGED;
        if (metrics.elevationVariance() >= 100.0) return TerrainProfile.MOUNTAINOUS;
        return TerrainProfile.MIXED;
    }
}

package com.wayfinder.geography.analysis;

import com.wayfinder.geography.model.TerrainMetrics;
import com.wayfinder.geography.model.TerrainSample;
import com.wayfinder.geography.model.TerrainSampleGrid;

import java.util.List;

public final class TerrainMetricsCalculator {
    public TerrainMetrics calculate(TerrainSampleGrid grid) {
        List<TerrainSample> samples = grid.samples();
        if (samples.isEmpty()) return new TerrainMetrics(0.0, 0.0, 0.0);

        double mean = samples.stream().mapToDouble(TerrainSample::surfaceY).average().orElse(0.0);
        double variance = samples.stream()
            .mapToDouble(sample -> {
                double delta = sample.surfaceY() - mean;
                return delta * delta;
            })
            .average().orElse(0.0);

        int min = samples.stream().mapToInt(TerrainSample::surfaceY).min().orElse(0);
        int max = samples.stream().mapToInt(TerrainSample::surfaceY).max().orElse(0);
        double horizontalScale = Math.max(grid.spacing(), Math.max(grid.width(), grid.depth()) * (double) grid.spacing());
        double ruggedness = Math.min(1.0, (max - min) / Math.max(1.0, horizontalScale * 0.5));

        return new TerrainMetrics(mean, variance, ruggedness);
    }
}

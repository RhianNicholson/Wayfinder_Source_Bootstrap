package com.wayfinder.geography.service;

import com.wayfinder.core.math.WorldBounds;
import com.wayfinder.geography.analysis.DefaultTerrainSampler;
import com.wayfinder.geography.analysis.TerrainMetricsCalculator;
import com.wayfinder.geography.analysis.TerrainProfileClassifier;
import com.wayfinder.geography.feature.HighPointDetector;
import com.wayfinder.geography.feature.LandmarkDetector;
import com.wayfinder.geography.feature.ObservationSiteDetector;
import com.wayfinder.geography.model.HighPointCandidate;
import com.wayfinder.geography.model.Landmark;
import com.wayfinder.geography.model.ObservationSite;
import com.wayfinder.geography.model.TerrainMetrics;
import com.wayfinder.geography.model.TerrainSample;
import com.wayfinder.geography.model.TerrainSampleGrid;
import com.wayfinder.geography.model.TerrainProfile;
import com.wayfinder.geography.world.WorldTerrainView;

import java.util.List;
import java.util.Objects;

/**
 * Application-level semantic geography composition for the development analyzer.
 * It observes and interprets terrain; it never creates civilization state or mutates the world.
 */
public final class TerrainAnalysisService {
    private final DefaultTerrainSampler sampler;
    private final TerrainMetricsCalculator metricsCalculator;
    private final TerrainProfileClassifier profileClassifier;
    private final HighPointDetector highPointDetector;
    private final LandmarkDetector landmarkDetector;
    private final ObservationSiteDetector observationSiteDetector;

    public TerrainAnalysisService() {
        this(
            new DefaultTerrainSampler(),
            new TerrainMetricsCalculator(),
            new TerrainProfileClassifier(),
            new HighPointDetector(),
            new LandmarkDetector(),
            new ObservationSiteDetector()
        );
    }

    public TerrainAnalysisService(
        DefaultTerrainSampler sampler,
        TerrainMetricsCalculator metricsCalculator,
        TerrainProfileClassifier profileClassifier,
        HighPointDetector highPointDetector,
        LandmarkDetector landmarkDetector,
        ObservationSiteDetector observationSiteDetector
    ) {
        this.sampler = Objects.requireNonNull(sampler, "sampler");
        this.metricsCalculator = Objects.requireNonNull(metricsCalculator, "metricsCalculator");
        this.profileClassifier = Objects.requireNonNull(profileClassifier, "profileClassifier");
        this.highPointDetector = Objects.requireNonNull(highPointDetector, "highPointDetector");
        this.landmarkDetector = Objects.requireNonNull(landmarkDetector, "landmarkDetector");
        this.observationSiteDetector = Objects.requireNonNull(observationSiteDetector, "observationSiteDetector");
    }

    public TerrainAnalysisReport analyze(WorldBounds bounds, int spacing, WorldTerrainView world) {
        long started = System.nanoTime();
        TerrainSampleGrid grid = sampler.sample(bounds, spacing, world);
        TerrainMetrics metrics = metricsCalculator.calculate(grid);
        TerrainProfile profile = profileClassifier.classify(metrics);
        List<TerrainSample> samples = grid.samples();

        List<HighPointCandidate> highPoints = highPointDetector.detect(grid);
        List<Landmark> landmarks = landmarkDetector.detect(highPoints, metrics);
        List<ObservationSite> observationSites = observationSiteDetector.detect(highPoints, landmarks);

        int min = samples.stream().mapToInt(TerrainSample::surfaceY).min().orElse(0);
        int max = samples.stream().mapToInt(TerrainSample::surfaceY).max().orElse(0);
        long elapsed = System.nanoTime() - started;

        return new TerrainAnalysisReport(
            bounds,
            spacing,
            samples.size(),
            min,
            max,
            metrics,
            profile,
            highPoints,
            landmarks,
            observationSites,
            elapsed
        );
    }
}

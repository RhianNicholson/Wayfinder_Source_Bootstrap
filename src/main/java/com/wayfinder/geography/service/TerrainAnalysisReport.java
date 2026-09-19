package com.wayfinder.geography.service;

import com.wayfinder.core.math.WorldBounds;
import com.wayfinder.geography.model.HighPointCandidate;
import com.wayfinder.geography.model.Landmark;
import com.wayfinder.geography.model.ObservationSite;
import com.wayfinder.geography.model.TerrainMetrics;
import com.wayfinder.geography.model.TerrainProfile;

import java.util.List;

public record TerrainAnalysisReport(
    WorldBounds bounds,
    int spacing,
    int sampleCount,
    int minimumElevation,
    int maximumElevation,
    TerrainMetrics metrics,
    TerrainProfile profile,
    List<HighPointCandidate> highPoints,
    List<Landmark> landmarks,
    List<ObservationSite> observationSites,
    long elapsedNanos
) {
    public TerrainAnalysisReport {
        highPoints = List.copyOf(highPoints);
        landmarks = List.copyOf(landmarks);
        observationSites = List.copyOf(observationSites);
    }

    public double elapsedMillis() {
        return elapsedNanos / 1_000_000.0;
    }

    public Landmark primaryLandmark() {
        return landmarks.isEmpty() ? null : landmarks.getFirst();
    }

    public ObservationSite bestObservationSite() {
        return observationSites.isEmpty() ? null : observationSites.getFirst();
    }
}

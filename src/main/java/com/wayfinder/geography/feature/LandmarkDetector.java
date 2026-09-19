package com.wayfinder.geography.feature;

import com.wayfinder.core.id.GeographicFeatureId;
import com.wayfinder.geography.model.HighPointCandidate;
import com.wayfinder.geography.model.Landmark;
import com.wayfinder.geography.model.LandmarkType;
import com.wayfinder.geography.model.TerrainMetrics;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

/** Converts only unusually salient high points into geographic landmark facts. */
public final class LandmarkDetector {
    private static final double MIN_SALIENCE = 0.36;
    private static final int MAX_LANDMARKS = 8;

    public List<Landmark> detect(List<HighPointCandidate> highPoints, TerrainMetrics metrics) {
        if (highPoints.isEmpty()) return List.of();

        double maxProminence = highPoints.stream().mapToDouble(HighPointCandidate::prominence).max().orElse(1.0);
        double maxIsolation = highPoints.stream().mapToDouble(HighPointCandidate::isolation).max().orElse(1.0);
        List<Landmark> landmarks = new ArrayList<>();

        for (HighPointCandidate point : highPoints) {
            double prominenceScore = clamp01(point.prominence() / Math.max(12.0, maxProminence));
            double isolationScore = clamp01(point.isolation() / Math.max(20.0, maxIsolation));
            double relativeElevation = clamp01((point.position().y() - metrics.meanElevation()) / 36.0);
            double salience = clamp01(
                prominenceScore * 0.45 +
                isolationScore * 0.35 +
                relativeElevation * 0.20
            );

            if (salience < MIN_SALIENCE) continue;

            LandmarkType type = classify(point, metrics);
            String stableKey = point.position().x() + ":" + point.position().y() + ":" + point.position().z() + ":" + type;
            GeographicFeatureId id = new GeographicFeatureId(
                UUID.nameUUIDFromBytes(stableKey.getBytes(StandardCharsets.UTF_8))
            );

            landmarks.add(new Landmark(
                id,
                point.position(),
                type,
                salience,
                point.prominence(),
                point.isolation()
            ));
        }

        return landmarks.stream()
            .sorted(Comparator.comparingDouble(Landmark::salience).reversed())
            .limit(MAX_LANDMARKS)
            .toList();
    }

    private static LandmarkType classify(HighPointCandidate point, TerrainMetrics metrics) {
        double aboveMean = point.position().y() - metrics.meanElevation();
        if (aboveMean >= 28 || point.prominence() >= 16) return LandmarkType.PROMINENT_PEAK;
        if (point.isolation() >= 18) return LandmarkType.ISOLATED_HILL;
        return LandmarkType.RIDGE_END;
    }

    private static double clamp01(double value) {
        return Math.max(0.0, Math.min(1.0, value));
    }
}

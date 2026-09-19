package com.wayfinder.geography.feature;

import com.wayfinder.geography.model.HighPointCandidate;
import com.wayfinder.geography.model.Landmark;
import com.wayfinder.geography.model.ObservationSite;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Finds coarse observation opportunities. This does not yet prove a precise Minecraft sightline;
 * that belongs to the next visibility pass.
 */
public final class ObservationSiteDetector {
    private static final double MIN_DISTANCE = 24.0;
    private static final double MAX_DISTANCE = 192.0;
    private static final int MAX_SITES = 12;

    public List<ObservationSite> detect(List<HighPointCandidate> highPoints, List<Landmark> landmarks) {
        if (landmarks.isEmpty() || highPoints.isEmpty()) return List.of();

        List<ObservationSite> result = new ArrayList<>();
        for (Landmark landmark : landmarks) {
            for (HighPointCandidate point : highPoints) {
                double distance = point.position().horizontalDistanceTo(landmark.anchor());
                if (distance < MIN_DISTANCE || distance > MAX_DISTANCE) continue;

                double distanceFit = 1.0 - Math.abs(distance - 88.0) / 104.0;
                distanceFit = clamp01(distanceFit);
                double slopeFit = clamp01(1.0 - point.slope());
                double elevationFit = clamp01((point.prominence() + point.isolation()) / 32.0);
                double visibilityPotential = clamp01(
                    landmark.salience() * 0.50 + elevationFit * 0.30 + distanceFit * 0.20
                );
                double buildability = clamp01(slopeFit * 0.80 + 0.20);
                double score = clamp01(
                    visibilityPotential * 0.60 + buildability * 0.25 + landmark.salience() * 0.15
                );

                if (score < 0.42) continue;
                result.add(new ObservationSite(
                    point.position(), landmark, visibilityPotential, buildability, score
                ));
            }
        }

        return result.stream()
            .sorted(Comparator.comparingDouble(ObservationSite::score).reversed())
            .limit(MAX_SITES)
            .toList();
    }

    private static double clamp01(double value) {
        return Math.max(0.0, Math.min(1.0, value));
    }
}

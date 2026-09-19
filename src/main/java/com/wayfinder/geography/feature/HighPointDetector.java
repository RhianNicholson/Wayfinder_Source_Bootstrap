package com.wayfinder.geography.feature;

import com.wayfinder.core.math.WorldPosition;
import com.wayfinder.geography.model.HighPointCandidate;
import com.wayfinder.geography.model.TerrainSample;
import com.wayfinder.geography.model.TerrainSampleGrid;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/** Detects terrain-supported local maxima without assigning civilization meaning. */
public final class HighPointDetector {
    private static final int NEIGHBORHOOD_RADIUS = 2;
    private static final double MIN_PROMINENCE = 6.0;

    public List<HighPointCandidate> detect(TerrainSampleGrid grid) {
        List<HighPointCandidate> result = new ArrayList<>();

        for (int gx = NEIGHBORHOOD_RADIUS; gx < grid.width() - NEIGHBORHOOD_RADIUS; gx++) {
            for (int gz = NEIGHBORHOOD_RADIUS; gz < grid.depth() - NEIGHBORHOOD_RADIUS; gz++) {
                TerrainSample center = grid.sampleAt(gx, gz);
                if (center == null || center.water()) continue;

                int maxNeighbor = Integer.MIN_VALUE;
                int minNeighbor = Integer.MAX_VALUE;
                double sum = 0.0;
                int count = 0;
                boolean strictMaximum = true;

                for (int dx = -NEIGHBORHOOD_RADIUS; dx <= NEIGHBORHOOD_RADIUS; dx++) {
                    for (int dz = -NEIGHBORHOOD_RADIUS; dz <= NEIGHBORHOOD_RADIUS; dz++) {
                        if (dx == 0 && dz == 0) continue;
                        TerrainSample neighbor = grid.sampleAt(gx + dx, gz + dz);
                        if (neighbor == null) continue;

                        int y = neighbor.surfaceY();
                        maxNeighbor = Math.max(maxNeighbor, y);
                        minNeighbor = Math.min(minNeighbor, y);
                        sum += y;
                        count++;
                        if (y >= center.surfaceY()) strictMaximum = false;
                    }
                }

                if (!strictMaximum || count == 0) continue;

                double meanNeighbor = sum / count;
                double prominence = center.surfaceY() - meanNeighbor;
                double isolation = center.surfaceY() - minNeighbor;
                if (prominence < MIN_PROMINENCE && isolation < MIN_PROMINENCE * 1.5) continue;

                result.add(new HighPointCandidate(
                    new WorldPosition(center.position().x(), center.surfaceY(), center.position().z()),
                    prominence,
                    isolation,
                    center.slope()
                ));
            }
        }

        return result.stream()
            .sorted(Comparator.comparingDouble(HighPointCandidate::prominence).reversed()
                .thenComparing(Comparator.comparingDouble(HighPointCandidate::isolation).reversed()))
            .toList();
    }
}

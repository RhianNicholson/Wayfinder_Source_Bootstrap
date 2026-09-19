package com.wayfinder.geography.visibility;

import com.wayfinder.core.math.WorldPosition;
import com.wayfinder.geography.model.Landmark;
import com.wayfinder.geography.world.WorldTerrainView;

/**
 * Coarse height-field sightline analysis.
 *
 * This intentionally distinguishes between:
 * - isolated terrain/sample noise
 * - sustained obstruction such as an intervening ridge
 *
 * A sustained blocked run is more significant than the same number of
 * scattered blocked samples.
 */
public final class HeightFieldVisibilityAnalyzer implements VisibilityAnalyzer {
    private final WorldTerrainView world;
    private final int raySamples;
    private final double eyeHeight;
    private final double clearance;

    public HeightFieldVisibilityAnalyzer(WorldTerrainView world, int raySamples) {
        this(world, raySamples, 1.62, 0.5);
    }

    public HeightFieldVisibilityAnalyzer(
            WorldTerrainView world,
            int raySamples,
            double eyeHeight,
            double clearance
    ) {
        if (raySamples < 4) {
            throw new IllegalArgumentException("raySamples must be >= 4");
        }

        this.world = world;
        this.raySamples = raySamples;
        this.eyeHeight = eyeHeight;
        this.clearance = clearance;
    }

    @Override
    public SightlineResult analyze(WorldPosition observer, Landmark target) {
        WorldPosition targetPos = target.anchor();

        double ox = observer.x() + 0.5;
        double oy = observer.y() + eyeHeight;
        double oz = observer.z() + 0.5;

        double tx = targetPos.x() + 0.5;
        double ty = targetPos.y() + 1.0;
        double tz = targetPos.z() + 0.5;

        int clearSamples = 0;
        int blockedSamples = 0;
        int currentBlockedRun = 0;
        int longestBlockedRun = 0;

        for (int i = 1; i <= raySamples; i++) {
            double t = i / (double) (raySamples + 1);

            int x = (int) Math.floor(lerp(ox, tx, t));
            int z = (int) Math.floor(lerp(oz, tz, t));
            double rayY = lerp(oy, ty, t);

            int terrainY = world.surfaceHeight(x, z);
            boolean clear = terrainY + clearance < rayY;

            if (clear) {
                clearSamples++;
                currentBlockedRun = 0;
            } else {
                blockedSamples++;
                currentBlockedRun++;
                longestBlockedRun = Math.max(longestBlockedRun, currentBlockedRun);
            }
        }

        double blockedFraction = blockedSamples / (double) raySamples;
        double longestBlockedFraction = longestBlockedRun / (double) raySamples;

        /*
         * Continuous obstruction receives an additional penalty.
         * This prevents a ridge occupying ~20% of a ray from being treated
         * as "visible" merely because most samples elsewhere are clear.
         *
         * Scattered single-sample obstruction remains relatively cheap,
         * which makes this coarse pass tolerant of height-field noise.
         */
        double obstruction = clamp(
                blockedFraction + (longestBlockedFraction * 0.75)
        );

        double score = clamp(1.0 - obstruction);

        /*
         * Coarse visibility threshold.
         *
         * Precise Minecraft ray validation can later distinguish borderline
         * cases. At this stage we reject sustained terrain obstruction.
         */
        boolean visible = score >= 0.75;

        return new SightlineResult(
                visible,
                score,
                clearSamples,
                raySamples,
                obstruction
        );
    }

    private static double lerp(double a, double b, double t) {
        return a + ((b - a) * t);
    }

    private static double clamp(double value) {
        return Math.max(0.0, Math.min(1.0, value));
    }
}

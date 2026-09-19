package com.wayfinder.structure.watchtower;

import com.wayfinder.core.math.WorldPosition;
import com.wayfinder.geography.world.WorldTerrainView;

/**
 * Solves the minimum observation-platform height that produces a usable
 * terrain sightline to the historical landmark target.
 *
 * The tower is not assigned an arbitrary visual height.
 */
public final class WatchtowerSightlineSolver {
    private final int samples;
    private final int minimumHeight;
    private final int maximumHeight;
    private final double requiredScore;

    public WatchtowerSightlineSolver(
            int samples,
            int minimumHeight,
            int maximumHeight,
            double requiredScore
    ) {
        if (samples < 4) {
            throw new IllegalArgumentException("samples must be >= 4");
        }
        if (minimumHeight < 2 || maximumHeight < minimumHeight) {
            throw new IllegalArgumentException("invalid tower height range");
        }

        this.samples = samples;
        this.minimumHeight = minimumHeight;
        this.maximumHeight = maximumHeight;
        this.requiredScore = requiredScore;
    }

    public WatchtowerSightlineResult solve(
            WorldPosition origin,
            WorldPosition target,
            WorldTerrainView world
    ) {
        for (int height = minimumHeight;
                height <= maximumHeight;
                height++) {

            int platformY = origin.y() + height;
            double score =
                    visibilityScore(
                            new WorldPosition(
                                    origin.x(),
                                    platformY,
                                    origin.z()
                            ),
                            target,
                            world
                    );

            if (score >= requiredScore) {
                return new WatchtowerSightlineResult(
                        true,
                        platformY,
                        height,
                        score
                );
            }
        }

        return new WatchtowerSightlineResult(
                false,
                origin.y() + maximumHeight,
                maximumHeight,
                visibilityScore(
                        new WorldPosition(
                                origin.x(),
                                origin.y() + maximumHeight,
                                origin.z()
                        ),
                        target,
                        world
                )
        );
    }

    private double visibilityScore(
            WorldPosition observer,
            WorldPosition target,
            WorldTerrainView world
    ) {
        double ox = observer.x() + 0.5;
        double oy = observer.y() + 1.62;
        double oz = observer.z() + 0.5;

        double tx = target.x() + 0.5;
        double ty = target.y() + 1.0;
        double tz = target.z() + 0.5;

        int blocked = 0;
        int currentRun = 0;
        int longestRun = 0;

        for (int i = 1; i <= samples; i++) {
            double t = i / (double) (samples + 1);

            int x = (int) Math.floor(
                    ox + ((tx - ox) * t)
            );
            int z = (int) Math.floor(
                    oz + ((tz - oz) * t)
            );
            double rayY =
                    oy + ((ty - oy) * t);

            boolean clear =
                    world.surfaceHeight(x, z) + 0.5
                            < rayY;

            if (clear) {
                currentRun = 0;
            } else {
                blocked++;
                currentRun++;
                longestRun =
                        Math.max(
                                longestRun,
                                currentRun
                        );
            }
        }

        double blockedFraction =
                blocked / (double) samples;
        double longestFraction =
                longestRun / (double) samples;

        return clamp(
                1.0
                        - blockedFraction
                        - (longestFraction * 0.75)
        );
    }

    private static double clamp(double value) {
        return Math.max(
                0.0,
                Math.min(
                        1.0,
                        value
                )
        );
    }
}

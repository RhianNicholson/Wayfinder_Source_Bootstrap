package com.wayfinder.civilization.direction;

import com.wayfinder.civilization.model.NodePurpose;
import com.wayfinder.civilization.state.CivilizationNode;
import com.wayfinder.core.math.WorldPosition;
import com.wayfinder.geography.world.WorldTerrainView;

import java.util.ArrayList;
import java.util.List;

/**
 * Generates coarse directional opportunities around a committed observation
 * node. Geography creates opportunities; it does not create justification.
 */
public final class DirectionCandidateGenerator {
    private static final int[] DISTANCES = {48, 80, 112, 144};
    private static final int[][] DIRECTIONS = {
            { 1,  0},
            { 1,  1},
            { 0,  1},
            {-1,  1},
            {-1,  0},
            {-1, -1},
            { 0, -1},
            { 1, -1}
    };

    private final WorldTerrainView world;

    public DirectionCandidateGenerator(WorldTerrainView world) {
        this.world = world;
    }

    public List<DirectionCandidate> generate(CivilizationNode destination) {
        if (destination.purpose() != NodePurpose.OBSERVATION) {
            return List.of();
        }

        List<DirectionCandidate> result = new ArrayList<>();
        WorldPosition target = destination.position();

        for (int[] direction : DIRECTIONS) {
            double length = Math.sqrt(
                    direction[0] * direction[0]
                            + direction[1] * direction[1]
            );

            for (int desiredDistance : DISTANCES) {
                int x = target.x()
                        + (int) Math.round(
                                direction[0] / length * desiredDistance
                        );
                int z = target.z()
                        + (int) Math.round(
                                direction[1] / length * desiredDistance
                        );
                int y = world.surfaceHeight(x, z);

                if (world.isWater(x, y, z)) {
                    continue;
                }

                WorldPosition position = new WorldPosition(x, y, z);
                double actualDistance =
                        position.horizontalDistanceTo(target);
                double buildability = buildabilityAt(x, z);
                double travelFit = travelFit(
                        position,
                        target,
                        actualDistance
                );
                double clarity = directionalClarity(actualDistance);

                result.add(
                        new DirectionCandidate(
                                candidateKey(
                                        destination,
                                        position
                                ),
                                position,
                                destination,
                                actualDistance,
                                buildability,
                                travelFit,
                                clarity
                        )
                );
            }
        }

        return List.copyOf(result);
    }

    private double buildabilityAt(int x, int z) {
        int center = world.surfaceHeight(x, z);
        int maxDelta = 0;

        for (int dx = -2; dx <= 2; dx += 2) {
            for (int dz = -2; dz <= 2; dz += 2) {
                int sample = world.surfaceHeight(
                        x + dx,
                        z + dz
                );
                maxDelta = Math.max(
                        maxDelta,
                        Math.abs(sample - center)
                );
            }
        }

        return clamp(1.0 - maxDelta / 8.0);
    }

    private static double travelFit(
            WorldPosition source,
            WorldPosition target,
            double distance
    ) {
        if (distance <= 0.0) {
            return 0.0;
        }

        double verticalCost =
                Math.abs(target.y() - source.y())
                        / Math.max(16.0, distance * 0.35);

        return clamp(1.0 - verticalCost);
    }

    private static double directionalClarity(double distance) {
        /*
         * Direction nodes should be far enough away to communicate a real
         * journey, but not so distant that the relationship becomes vague.
         * The first vertical slice favors roughly 80 blocks.
         */
        return clamp(
                1.0 - Math.abs(distance - 80.0) / 96.0
        );
    }

    private static String candidateKey(
            CivilizationNode destination,
            WorldPosition position
    ) {
        return "DIR:"
                + destination.id().value()
                + ":"
                + position.x()
                + ":"
                + position.y()
                + ":"
                + position.z();
    }

    private static double clamp(double value) {
        return Math.max(0.0, Math.min(1.0, value));
    }
}

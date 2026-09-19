package com.wayfinder.structure.model;

import com.wayfinder.core.math.WorldPosition;

/**
 * Eight-way semantic facing derived from historical targets.
 *
 * This is deliberately independent of Minecraft's Direction enum so the
 * structure-planning layer remains engine-domain code.
 */
public enum HorizontalFacing {
    NORTH(0, -1),
    NORTH_EAST(1, -1),
    EAST(1, 0),
    SOUTH_EAST(1, 1),
    SOUTH(0, 1),
    SOUTH_WEST(-1, 1),
    WEST(-1, 0),
    NORTH_WEST(-1, -1);

    private final int stepX;
    private final int stepZ;

    HorizontalFacing(int stepX, int stepZ) {
        this.stepX = stepX;
        this.stepZ = stepZ;
    }

    public int stepX() {
        return stepX;
    }

    public int stepZ() {
        return stepZ;
    }

    public static HorizontalFacing toward(
            WorldPosition origin,
            WorldPosition target
    ) {
        long dx = (long) target.x() - origin.x();
        long dz = (long) target.z() - origin.z();

        if (dx == 0 && dz == 0) {
            throw new IllegalArgumentException(
                    "A structure cannot derive facing from a coincident target"
            );
        }

        double angle =
                Math.atan2(
                        dz,
                        dx
                );

        int octant =
                Math.floorMod(
                        (int) Math.round(
                                angle
                                        / (Math.PI / 4.0)
                        ),
                        8
                );

        return switch (octant) {
            case 0 -> EAST;
            case 1 -> SOUTH_EAST;
            case 2 -> SOUTH;
            case 3 -> SOUTH_WEST;
            case 4 -> WEST;
            case 5 -> NORTH_WEST;
            case 6 -> NORTH;
            case 7 -> NORTH_EAST;
            default -> throw new IllegalStateException();
        };
    }
}

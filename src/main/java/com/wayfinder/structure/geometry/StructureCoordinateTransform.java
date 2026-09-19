package com.wayfinder.structure.geometry;

import com.wayfinder.core.math.WorldPosition;
import com.wayfinder.structure.model.HorizontalFacing;

/**
 * Converts local structure coordinates into world coordinates.
 *
 * Eight-way facings are supported directly.
 */
public final class StructureCoordinateTransform {

    public WorldPosition toWorld(
            WorldPosition origin,
            HorizontalFacing facing,
            LocalStructurePosition local
    ) {
        int forwardX = facing.stepX();
        int forwardZ = facing.stepZ();

        /*
         * Clockwise local-right vector.
         *
         * For cardinal facings this is a unit vector. For diagonal facings
         * both axes remain integral, producing a deliberate diagonal masonry
         * footprint rather than an interpolated/cardinalized structure.
         */
        int rightX = -forwardZ;
        int rightZ = forwardX;

        return new WorldPosition(
                origin.x()
                        + local.forward() * forwardX
                        + local.right() * rightX,
                origin.y() + local.up(),
                origin.z()
                        + local.forward() * forwardZ
                        + local.right() * rightZ
        );
    }
}

package com.wayfinder.structure.watchtower;

import com.wayfinder.structure.geometry.StructureGeometryPlan;

public record WatchtowerGeometryPlan(
        StructureGeometryPlan geometry,
        WatchtowerSightlineResult sightline
) {
    public WatchtowerGeometryPlan {
        if (geometry == null) {
            throw new IllegalArgumentException("geometry is required");
        }
        if (sightline == null || !sightline.solved()) {
            throw new IllegalArgumentException(
                    "Watchtower geometry requires a solved sightline"
            );
        }
    }
}

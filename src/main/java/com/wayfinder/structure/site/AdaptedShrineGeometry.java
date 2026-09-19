package com.wayfinder.structure.site;

import com.wayfinder.structure.geometry.StructureGeometryPlan;

public record AdaptedShrineGeometry(
        StructureGeometryPlan geometry,
        ShrineSiteValidationResult validation,
        int addedFoundationBlocks
) {
    public AdaptedShrineGeometry {
        if (geometry == null) {
            throw new IllegalArgumentException("geometry is required");
        }
        if (validation == null || !validation.accepted()) {
            throw new IllegalArgumentException(
                    "Adapted Shrine geometry requires an accepted site"
            );
        }
        if (addedFoundationBlocks < 0) {
            throw new IllegalArgumentException(
                    "addedFoundationBlocks cannot be negative"
            );
        }
    }
}

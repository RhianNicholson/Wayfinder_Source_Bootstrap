package com.wayfinder.structure.watchtower.site;

import com.wayfinder.structure.geometry.StructureGeometryPlan;
import com.wayfinder.structure.watchtower.WatchtowerSightlineResult;

public record AdaptedWatchtowerGeometry(
        StructureGeometryPlan geometry,
        WatchtowerSightlineResult sightline,
        WatchtowerSiteValidationResult validation,
        int addedSupportBlocks
) {
    public AdaptedWatchtowerGeometry {
        if (geometry == null || sightline == null || validation == null) {
            throw new IllegalArgumentException("geometry, sightline, and validation are required");
        }
        if (!sightline.solved() || !validation.accepted()) {
            throw new IllegalArgumentException("adapted tower requires solved sightline and accepted site");
        }
        if (addedSupportBlocks < 0) {
            throw new IllegalArgumentException("addedSupportBlocks cannot be negative");
        }
    }
}

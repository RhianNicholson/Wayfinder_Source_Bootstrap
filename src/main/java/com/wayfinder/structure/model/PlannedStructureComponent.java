package com.wayfinder.structure.model;

import java.util.Set;

/**
 * Semantic component of a future physical structure.
 *
 * Geometry/block selection comes later. At this boundary every component must
 * already justify its existence.
 */
public record PlannedStructureComponent(
        StructureComponentRole role,
        Set<StructurePurpose> purposes
) {
    public PlannedStructureComponent {
        if (role == null) {
            throw new IllegalArgumentException("role is required");
        }

        purposes = Set.copyOf(purposes);

        if (purposes.isEmpty()) {
            throw new IllegalArgumentException(
                    "Every planned component must serve a structure purpose"
            );
        }
    }
}

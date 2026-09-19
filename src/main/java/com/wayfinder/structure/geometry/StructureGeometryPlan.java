package com.wayfinder.structure.geometry;

import com.wayfinder.structure.model.StructureMaterializationPlan;

import java.util.List;

/**
 * A dry-run physical block layout.
 *
 * This is deliberately still Minecraft-independent.
 */
public record StructureGeometryPlan(
        StructureMaterializationPlan materializationPlan,
        List<PlannedBlockCell> blocks
) {
    public StructureGeometryPlan {
        if (materializationPlan == null) {
            throw new IllegalArgumentException(
                    "materializationPlan is required"
            );
        }

        blocks = List.copyOf(blocks);

        if (blocks.isEmpty()) {
            throw new IllegalArgumentException(
                    "A geometry plan must contain planned blocks"
            );
        }

        long unique =
                blocks.stream()
                        .map(PlannedBlockCell::position)
                        .distinct()
                        .count();

        if (unique != blocks.size()) {
            throw new IllegalArgumentException(
                    "Geometry plan contains overlapping block cells"
            );
        }
    }
}

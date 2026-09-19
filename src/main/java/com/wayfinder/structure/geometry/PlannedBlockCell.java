package com.wayfinder.structure.geometry;

import com.wayfinder.core.math.WorldPosition;

/**
 * One dry-run block cell in world coordinates.
 *
 * The cell describes intent only. It cannot mutate a Minecraft world.
 */
public record PlannedBlockCell(
        WorldPosition position,
        BlockMaterialRole materialRole,
        BlockFunction function
) {
    public PlannedBlockCell {
        if (position == null) {
            throw new IllegalArgumentException("position is required");
        }
        if (materialRole == null) {
            throw new IllegalArgumentException("materialRole is required");
        }
        if (function == null) {
            throw new IllegalArgumentException("function is required");
        }
    }
}

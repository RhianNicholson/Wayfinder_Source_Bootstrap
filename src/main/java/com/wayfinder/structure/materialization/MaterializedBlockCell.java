package com.wayfinder.structure.materialization;

import com.wayfinder.core.math.WorldPosition;
import com.wayfinder.structure.geometry.BlockFunction;
import com.wayfinder.structure.geometry.BlockMaterialRole;

public record MaterializedBlockCell(
        WorldPosition position,
        BlockMaterialRole materialRole,
        BlockFunction function
) {
    public MaterializedBlockCell {
        if (position == null || materialRole == null || function == null) {
            throw new IllegalArgumentException("materialized cell fields are required");
        }
    }
}

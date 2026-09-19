package com.wayfinder.structure.model;

import java.util.List;

/**
 * Minecraft-independent physical contract.
 *
 * A renderer may later convert this plan into actual blocks, but it may not
 * change the historical source, semantic target, facing, or purposes.
 */
public record StructureMaterializationPlan(
        StructureIntent intent,
        List<PlannedStructureComponent> components
) {
    public StructureMaterializationPlan {
        if (intent == null) {
            throw new IllegalArgumentException("intent is required");
        }

        components = List.copyOf(components);

        if (components.isEmpty()) {
            throw new IllegalArgumentException(
                    "A materialization plan must contain purposeful components"
            );
        }
    }
}

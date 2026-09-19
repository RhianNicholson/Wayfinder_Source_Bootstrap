package com.wayfinder.structure.model;

import com.wayfinder.core.id.NodeId;
import com.wayfinder.core.math.WorldPosition;

import java.util.Set;

/**
 * Immutable translation from civilization truth into physical intent.
 *
 * This says what a future structure must communicate and where its semantic
 * axis points. It does not specify blocks.
 */
public record StructureIntent(
        NodeId sourceNodeId,
        StructureArchetype archetype,
        WorldPosition origin,
        WorldPosition semanticTarget,
        HorizontalFacing facing,
        Set<StructurePurpose> purposes
) {
    public StructureIntent {
        if (sourceNodeId == null) {
            throw new IllegalArgumentException("sourceNodeId is required");
        }
        if (archetype == null) {
            throw new IllegalArgumentException("archetype is required");
        }
        if (origin == null) {
            throw new IllegalArgumentException("origin is required");
        }
        if (semanticTarget == null) {
            throw new IllegalArgumentException("semanticTarget is required");
        }
        if (facing == null) {
            throw new IllegalArgumentException("facing is required");
        }

        purposes = Set.copyOf(purposes);

        if (purposes.isEmpty()) {
            throw new IllegalArgumentException(
                    "A structure intent must have at least one physical purpose"
            );
        }
    }
}

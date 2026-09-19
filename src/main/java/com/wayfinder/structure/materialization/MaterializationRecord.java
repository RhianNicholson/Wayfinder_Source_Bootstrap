package com.wayfinder.structure.materialization;

import com.wayfinder.core.id.NodeId;
import com.wayfinder.structure.model.StructureArchetype;

import java.util.List;

public record MaterializationRecord(
        NodeId sourceNodeId,
        StructureArchetype archetype,
        int formatVersion,
        int paletteVersion,
        MaterializationCondition condition,
        List<MaterializedBlockCell> originalCells
) {
    public MaterializationRecord {
        if (sourceNodeId == null || archetype == null || condition == null) {
            throw new IllegalArgumentException("materialization identity is required");
        }
        if (formatVersion < 1 || paletteVersion < 1) {
            throw new IllegalArgumentException("versions must be positive");
        }
        originalCells = List.copyOf(originalCells);
        if (originalCells.isEmpty()) {
            throw new IllegalArgumentException("materialization must preserve original geometry");
        }
    }
}

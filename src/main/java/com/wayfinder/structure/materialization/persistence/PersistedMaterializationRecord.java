package com.wayfinder.structure.materialization.persistence;

import java.util.List;

public record PersistedMaterializationRecord(
        String sourceNodeId,
        String archetype,
        int formatVersion,
        int paletteVersion,
        String condition,
        List<PersistedMaterializedCell> originalCells
) {
    public PersistedMaterializationRecord {
        originalCells = List.copyOf(originalCells);
    }
}

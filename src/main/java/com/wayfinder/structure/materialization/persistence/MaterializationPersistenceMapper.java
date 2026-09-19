package com.wayfinder.structure.materialization.persistence;

import com.wayfinder.core.id.NodeId;
import com.wayfinder.core.math.WorldPosition;
import com.wayfinder.structure.geometry.BlockFunction;
import com.wayfinder.structure.geometry.BlockMaterialRole;
import com.wayfinder.structure.materialization.*;
import com.wayfinder.structure.model.StructureArchetype;

import java.util.List;
import java.util.UUID;

public final class MaterializationPersistenceMapper {
    private MaterializationPersistenceMapper() {}

    public static List<PersistedMaterializationRecord> toPersisted(
            MaterializationState state
    ) {
        return state.records().stream().map(record ->
                new PersistedMaterializationRecord(
                        record.sourceNodeId().value().toString(),
                        record.archetype().name(),
                        record.formatVersion(),
                        record.paletteVersion(),
                        record.condition().name(),
                        record.originalCells().stream().map(cell ->
                                new PersistedMaterializedCell(
                                        cell.position().x(),
                                        cell.position().y(),
                                        cell.position().z(),
                                        cell.materialRole().name(),
                                        cell.function().name()
                                )
                        ).toList()
                )
        ).toList();
    }

    public static MaterializationState toDomain(
            List<PersistedMaterializationRecord> records
    ) {
        return new MaterializationState(records.stream().map(record ->
                new MaterializationRecord(
                        new NodeId(UUID.fromString(record.sourceNodeId())),
                        StructureArchetype.valueOf(record.archetype()),
                        record.formatVersion(),
                        record.paletteVersion(),
                        MaterializationCondition.valueOf(record.condition()),
                        record.originalCells().stream().map(cell ->
                                new MaterializedBlockCell(
                                        new WorldPosition(cell.x(), cell.y(), cell.z()),
                                        BlockMaterialRole.valueOf(cell.materialRole()),
                                        BlockFunction.valueOf(cell.function())
                                )
                        ).toList()
                )
        ).toList());
    }
}

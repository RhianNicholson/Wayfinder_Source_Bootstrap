package com.wayfinder.structure.materialization;

import com.wayfinder.structure.geometry.StructureGeometryPlan;

public final class MaterializationRecordFactory {
    public static final int CURRENT_FORMAT_VERSION = 1;
    public static final int CURRENT_PALETTE_VERSION = 1;

    public MaterializationRecord fromPlacedGeometry(
            StructureGeometryPlan geometry
    ) {
        var intent = geometry.materializationPlan().intent();

        return new MaterializationRecord(
                intent.sourceNodeId(),
                intent.archetype(),
                CURRENT_FORMAT_VERSION,
                CURRENT_PALETTE_VERSION,
                MaterializationCondition.INTACT,
                geometry.blocks().stream()
                        .map(cell -> new MaterializedBlockCell(
                                cell.position(),
                                cell.materialRole(),
                                cell.function()
                        ))
                        .toList()
        );
    }
}

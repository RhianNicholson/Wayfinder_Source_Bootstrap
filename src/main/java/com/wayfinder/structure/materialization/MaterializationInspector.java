package com.wayfinder.structure.materialization;

public final class MaterializationInspector {

    public MaterializationInspection inspect(
            MaterializationRecord record,
            MaterializationBlockView world
    ) {
        int matching = 0;

        for (var cell : record.originalCells()) {
            if (world.matches(record, cell)) {
                matching++;
            }
        }

        MaterializationCondition condition;
        if (matching == record.originalCells().size()) {
            condition = MaterializationCondition.INTACT;
        } else if (matching == 0) {
            condition = MaterializationCondition.LOST;
        } else {
            condition = MaterializationCondition.DAMAGED;
        }

        return new MaterializationInspection(
                condition,
                matching,
                record.originalCells().size()
        );
    }
}

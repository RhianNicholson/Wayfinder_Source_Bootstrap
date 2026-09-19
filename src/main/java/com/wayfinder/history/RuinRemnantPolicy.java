package com.wayfinder.history;

import com.wayfinder.structure.geometry.BlockFunction;
import com.wayfinder.structure.materialization.MaterializationRecord;
import com.wayfinder.structure.materialization.MaterializedBlockCell;
import com.wayfinder.structure.model.StructureArchetype;

import java.util.List;

/**
 * Determines which ORIGINAL recorded cells may survive a route-loss event.
 *
 * It never creates debris or new geometry. Damage may remove information;
 * it may not invent information.
 */
public final class RuinRemnantPolicy {

    public List<MaterializedBlockCell> survivingCells(MaterializationRecord record) {
        if (record.archetype() != StructureArchetype.WATCHTOWER) {
            return List.of();
        }

        return record.originalCells().stream()
                .filter(cell ->
                        cell.function() == BlockFunction.TOWER_FOUNDATION
                        || (cell.function() == BlockFunction.TOWER_SUPPORT
                            && isLowSupport(record, cell)))
                .toList();
    }

    private boolean isLowSupport(
            MaterializationRecord record,
            MaterializedBlockCell cell
    ) {
        int lowestY = record.originalCells().stream()
                .filter(c -> c.function() == BlockFunction.TOWER_SUPPORT)
                .mapToInt(c -> c.position().y())
                .min()
                .orElse(Integer.MIN_VALUE);

        // Preserve only the bottom two recorded support courses.
        return cell.position().y() <= lowestY + 1;
    }
}

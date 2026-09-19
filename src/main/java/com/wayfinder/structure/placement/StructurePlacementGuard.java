package com.wayfinder.structure.placement;

import com.wayfinder.structure.geometry.BlockFunction;
import com.wayfinder.structure.geometry.StructureGeometryPlan;

public final class StructurePlacementGuard {

    public StructurePlacementDecision evaluate(
            StructureGeometryPlan geometry,
            StructurePlacementView world
    ) {
        int matching = 0;
        int empty = 0;
        int replaceableFoundation = 0;
        int blocked = 0;
        int matchingUpperStructure = 0;

        for (var cell : geometry.blocks()) {
            StructureCellState state = world.classify(cell);

            switch (state) {
                case MATCHING_PLANNED_BLOCK -> {
                    matching++;
                    if (!isFoundation(cell.function())) {
                        matchingUpperStructure++;
                    }
                }
                case EMPTY -> empty++;
                case REPLACEABLE_FOUNDATION -> replaceableFoundation++;
                case BLOCKED -> blocked++;
            }
        }

        StructurePlacementStatus status;

        if (matching == geometry.blocks().size()) {
            status = StructurePlacementStatus.ALREADY_PRESENT;
        } else if (blocked > 0) {
            status = StructurePlacementStatus.BLOCKED;
        } else if (matchingUpperStructure > 0) {
            // Never silently rebuild potentially historical damage.
            status = StructurePlacementStatus.PARTIAL_EXISTING_STRUCTURE;
        } else {
            status = StructurePlacementStatus.READY;
        }

        return new StructurePlacementDecision(
                status,
                matching,
                empty,
                replaceableFoundation,
                blocked
        );
    }

    private static boolean isFoundation(
            BlockFunction function
    ) {
        return function == BlockFunction.FOUNDATION
                || function == BlockFunction.TOWER_FOUNDATION;
    }
}

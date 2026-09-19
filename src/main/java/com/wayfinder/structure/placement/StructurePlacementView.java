package com.wayfinder.structure.placement;

import com.wayfinder.structure.geometry.PlannedBlockCell;

public interface StructurePlacementView {
    StructureCellState classify(PlannedBlockCell cell);
}

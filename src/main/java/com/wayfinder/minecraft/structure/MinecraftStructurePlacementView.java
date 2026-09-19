package com.wayfinder.minecraft.structure;

import com.wayfinder.structure.geometry.BlockFunction;
import com.wayfinder.structure.geometry.PlannedBlockCell;
import com.wayfinder.structure.placement.StructureCellState;
import com.wayfinder.structure.placement.StructurePlacementView;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;

public final class MinecraftStructurePlacementView
        implements StructurePlacementView {

    private final ServerLevel level;
    private final WaystoneShrineBlockPalette palette;

    public MinecraftStructurePlacementView(
            ServerLevel level,
            WaystoneShrineBlockPalette palette
    ) {
        this.level = level;
        this.palette = palette;
    }

    @Override
    public StructureCellState classify(PlannedBlockCell cell) {
        BlockPos pos = new BlockPos(
                cell.position().x(),
                cell.position().y(),
                cell.position().z()
        );

        var current = level.getBlockState(pos);
        var desired = palette.stateFor(cell.materialRole());

        if (current.equals(desired)) {
            return StructureCellState.MATCHING_PLANNED_BLOCK;
        }

        if (cell.function() == BlockFunction.FOUNDATION) {
            if (current.isAir()
                    || current.isCollisionShapeFullBlock(level, pos)) {
                return StructureCellState.REPLACEABLE_FOUNDATION;
            }

            return StructureCellState.BLOCKED;
        }

        return current.isAir()
                ? StructureCellState.EMPTY
                : StructureCellState.BLOCKED;
    }
}

package com.wayfinder.minecraft.structure;

import com.wayfinder.structure.geometry.StructureGeometryPlan;
import com.wayfinder.structure.placement.StructurePlacementDecision;
import com.wayfinder.structure.placement.StructurePlacementGuard;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;

public final class WaystoneShrineWorldRenderer {
    private final WaystoneShrineBlockPalette palette;
    private final StructurePlacementGuard guard;

    public WaystoneShrineWorldRenderer(
            WaystoneShrineBlockPalette palette,
            StructurePlacementGuard guard
    ) {
        this.palette = palette;
        this.guard = guard;
    }

    public Result place(
            ServerLevel level,
            StructureGeometryPlan geometry
    ) {
        StructurePlacementDecision decision =
                guard.evaluate(
                        geometry,
                        new MinecraftStructurePlacementView(
                                level,
                                palette
                        )
                );

        if (!decision.mayPlace()) {
            return new Result(false, 0, decision);
        }

        int changed = 0;

        for (var cell : geometry.blocks()) {
            BlockPos pos = new BlockPos(
                    cell.position().x(),
                    cell.position().y(),
                    cell.position().z()
            );

            var desired = palette.stateFor(cell.materialRole());

            if (!level.getBlockState(pos).equals(desired)) {
                level.setBlock(pos, desired, 3);
                changed++;
            }
        }

        return new Result(true, changed, decision);
    }

    public record Result(
            boolean placed,
            int changedBlocks,
            StructurePlacementDecision decision
    ) {}
}

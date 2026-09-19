package com.wayfinder.minecraft.history;

import com.wayfinder.history.RuinRemnantPolicy;
import com.wayfinder.minecraft.structure.WatchtowerBlockPalette;
import com.wayfinder.minecraft.structure.WaystoneShrineBlockPalette;
import com.wayfinder.structure.materialization.MaterializationRecord;
import com.wayfinder.structure.model.StructureArchetype;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Blocks;

import java.util.HashSet;

public final class MinecraftRouteLossApplier {

    /**
     * Applies historical loss while preserving deterministic remnants selected
     * strictly from the structure's original recorded geometry.
     */
    public int removeRecordedStructure(ServerLevel level, MaterializationRecord record) {
        var survivors = new HashSet<>(
                new RuinRemnantPolicy().survivingCells(record)
        );

        int removed = 0;
        for (var cell : record.originalCells()) {
            if (survivors.contains(cell)) {
                continue;
            }

            var expected =
                    record.archetype() == StructureArchetype.WAYSTONE_SHRINE
                            ? new WaystoneShrineBlockPalette().stateFor(cell.materialRole())
                            : new WatchtowerBlockPalette().stateFor(cell.materialRole());

            var pos = new BlockPos(
                    cell.position().x(),
                    cell.position().y(),
                    cell.position().z());

            // Never delete player/world blocks that no longer match history.
            if (level.getBlockState(pos).equals(expected)
                    && level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3)) {
                removed++;
            }
        }
        return removed;
    }
}

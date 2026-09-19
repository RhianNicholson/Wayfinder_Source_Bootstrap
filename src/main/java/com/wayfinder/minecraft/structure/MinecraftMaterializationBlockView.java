package com.wayfinder.minecraft.structure;

import com.wayfinder.structure.materialization.MaterializationBlockView;
import com.wayfinder.structure.materialization.MaterializationRecord;
import com.wayfinder.structure.materialization.MaterializedBlockCell;
import com.wayfinder.structure.model.StructureArchetype;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;

public final class MinecraftMaterializationBlockView
        implements MaterializationBlockView {

    private final ServerLevel level;
    private final WaystoneShrineBlockPalette shrinePalette;
    private final WatchtowerBlockPalette watchtowerPalette;

    public MinecraftMaterializationBlockView(
            ServerLevel level
    ) {
        this.level = level;
        this.shrinePalette = new WaystoneShrineBlockPalette();
        this.watchtowerPalette = new WatchtowerBlockPalette();
    }

    @Override
    public boolean matches(
            MaterializationRecord record,
            MaterializedBlockCell cell
    ) {
        var desired =
                record.archetype() == StructureArchetype.WAYSTONE_SHRINE
                        ? shrinePalette.stateFor(cell.materialRole())
                        : watchtowerPalette.stateFor(cell.materialRole());

        var pos = new BlockPos(
                cell.position().x(),
                cell.position().y(),
                cell.position().z()
        );

        return level.getBlockState(pos).equals(desired);
    }
}

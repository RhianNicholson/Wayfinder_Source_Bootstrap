package com.wayfinder.minecraft.history;

import com.wayfinder.history.RuinRemnantPolicy;
import com.wayfinder.minecraft.structure.MinecraftMaterializationBlockView;
import com.wayfinder.structure.materialization.MaterializationRecord;
import net.minecraft.server.level.ServerLevel;

import java.util.HashSet;

/**
 * Determines whether a ROUTE_LOSS consequence still has physical work to do.
 *
 * A completed Wayfinder ruin may truthfully remain DAMAGED because approved
 * foundation/support remnants survive. Therefore MaterializationCondition
 * cannot be used as the completion marker.
 *
 * Instead, exact recorded block truth is inspected. Physicalization is pending
 * only while at least one original cell that is NOT an approved ruin survivor
 * still matches its recorded material.
 */
public final class MinecraftRouteLossPhysicalizationInspector {

    public boolean requiresPhysicalization(
            ServerLevel level,
            MaterializationRecord record
    ) {
        var survivors = new HashSet<>(
                new RuinRemnantPolicy().survivingCells(record));
        var view = new MinecraftMaterializationBlockView(level);

        return record.originalCells().stream()
                .filter(cell -> !survivors.contains(cell))
                .anyMatch(cell -> view.matches(record, cell));
    }
}

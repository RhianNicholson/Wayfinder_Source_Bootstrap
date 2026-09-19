package com.wayfinder.structure.watchtower.site;

import com.wayfinder.core.math.WorldPosition;
import com.wayfinder.geography.world.WorldTerrainView;
import com.wayfinder.structure.geometry.BlockFunction;
import com.wayfinder.structure.geometry.BlockMaterialRole;
import com.wayfinder.structure.geometry.PlannedBlockCell;
import com.wayfinder.structure.geometry.StructureGeometryPlan;
import com.wayfinder.structure.watchtower.WatchtowerGeometryPlan;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Extends only tower supports/foundations downward to natural terrain.
 *
 * Platform height, facing, target, and upper geometry never move.
 */
public final class WatchtowerTerrainAdapter {

    public AdaptedWatchtowerGeometry adapt(
            WatchtowerGeometryPlan tower,
            WatchtowerSiteValidationResult validation,
            WorldTerrainView world
    ) {
        if (!validation.accepted()) {
            throw new IllegalArgumentException("rejected tower site cannot be adapted");
        }

        Map<WorldPosition, PlannedBlockCell> blocks = new LinkedHashMap<>();
        for (var cell : tower.geometry().blocks()) {
            blocks.put(cell.position(), cell);
        }

        int added = 0;

        for (var cell : tower.geometry().blocks()) {
            if (cell.function() != BlockFunction.TOWER_FOUNDATION) {
                continue;
            }

            int surfaceY = world.surfaceHeight(
                    cell.position().x(),
                    cell.position().z()
            ) - 1;

            for (int y = cell.position().y() - 1; y >= surfaceY; y--) {
                WorldPosition pos = new WorldPosition(
                        cell.position().x(),
                        y,
                        cell.position().z()
                );

                if (blocks.putIfAbsent(
                        pos,
                        new PlannedBlockCell(
                                pos,
                                BlockMaterialRole.TOWER_FOUNDATION_STONE,
                                BlockFunction.TOWER_FOUNDATION
                        )
                ) == null) {
                    added++;
                }
            }
        }

        return new AdaptedWatchtowerGeometry(
                new StructureGeometryPlan(
                        tower.geometry().materializationPlan(),
                        List.copyOf(blocks.values())
                ),
                tower.sightline(),
                validation,
                added
        );
    }
}

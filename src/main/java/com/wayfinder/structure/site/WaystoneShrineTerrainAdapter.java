package com.wayfinder.structure.site;

import com.wayfinder.core.math.WorldPosition;
import com.wayfinder.geography.world.WorldTerrainView;
import com.wayfinder.structure.geometry.BlockFunction;
import com.wayfinder.structure.geometry.BlockMaterialRole;
import com.wayfinder.structure.geometry.PlannedBlockCell;
import com.wayfinder.structure.geometry.StructureGeometryPlan;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Adapts only the Shrine foundation downward into terrain.
 *
 * Historical position, facing, approach, axis, glyph surface, and upper
 * architecture are immutable.
 */
public final class WaystoneShrineTerrainAdapter {

    public AdaptedShrineGeometry adapt(
            StructureGeometryPlan geometry,
            ShrineSiteValidationResult validation,
            WorldTerrainView world
    ) {
        if (!validation.accepted()) {
            throw new IllegalArgumentException(
                    "Rejected Shrine sites may not be terrain-adapted"
            );
        }

        Map<WorldPosition, PlannedBlockCell> blocks =
                new LinkedHashMap<>();

        for (var block : geometry.blocks()) {
            blocks.put(
                    block.position(),
                    block
            );
        }

        int added = 0;

        for (var block : geometry.blocks()) {
            if (block.function()
                    != BlockFunction.FOUNDATION) {
                continue;
            }

            int surfaceY =
                    world.surfaceHeight(
                            block.position().x(),
                            block.position().z()
                    ) - 1;

            /*
             * A foundation cell is already planned at the Shrine's base.
             * Where terrain falls away, extend masonry downward until it meets
             * the actual surface. Nothing above the historical base is moved.
             */
            for (int y = block.position().y() - 1;
                    y >= surfaceY;
                    y--) {

                WorldPosition position =
                        new WorldPosition(
                                block.position().x(),
                                y,
                                block.position().z()
                        );

                if (blocks.putIfAbsent(
                        position,
                        new PlannedBlockCell(
                                position,
                                BlockMaterialRole.FOUNDATION_STONE,
                                BlockFunction.FOUNDATION
                        )
                ) == null) {
                    added++;
                }
            }
        }

        return new AdaptedShrineGeometry(
                new StructureGeometryPlan(
                        geometry.materializationPlan(),
                        List.copyOf(
                                blocks.values()
                        )
                ),
                validation,
                added
        );
    }
}

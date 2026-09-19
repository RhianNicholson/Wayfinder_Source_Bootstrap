package com.wayfinder.structure.shrine;

import com.wayfinder.structure.geometry.BlockFunction;
import com.wayfinder.structure.geometry.BlockMaterialRole;
import com.wayfinder.structure.geometry.LocalStructurePosition;
import com.wayfinder.structure.geometry.PlannedBlockCell;
import com.wayfinder.structure.geometry.StructureCoordinateTransform;
import com.wayfinder.structure.geometry.StructureGeometryPlan;
import com.wayfinder.structure.model.StructureArchetype;
import com.wayfinder.structure.model.StructureMaterializationPlan;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * First executable physical interpretation of a Waystone Shrine.
 *
 * Design constraints:
 * - architecture itself communicates direction
 * - approach leads into the semantic axis
 * - glyph surface reinforces, but does not replace, architectural meaning
 * - every block has a reason
 * - no decorative filler is emitted
 */
public final class WaystoneShrineGeometrySolver {
    private final StructureCoordinateTransform transform =
            new StructureCoordinateTransform();

    public StructureGeometryPlan solve(
            StructureMaterializationPlan plan
    ) {
        if (plan.intent().archetype()
                != StructureArchetype.WAYSTONE_SHRINE) {
            throw new IllegalArgumentException(
                    "WaystoneShrineGeometrySolver requires a WAYSTONE_SHRINE plan"
            );
        }

        /*
         * LinkedHashMap makes accidental overlap explicit and deterministic.
         * Higher-semantic components are inserted only where a lower role has
         * not already claimed the cell.
         */
        Map<com.wayfinder.core.math.WorldPosition, PlannedBlockCell> blocks =
                new LinkedHashMap<>();

        foundation(plan, blocks);
        approach(plan, blocks);
        directionalAxis(plan, blocks);
        shrineBody(plan, blocks);
        glyphSurface(plan, blocks);

        return new StructureGeometryPlan(
                plan,
                List.copyOf(blocks.values())
        );
    }

    /**
     * 5x5 grounded plinth around the node origin.
     *
     * The corners are intentionally omitted. The result feels less like a
     * generated square pad while still giving the shrine a stable footprint.
     */
    private void foundation(
            StructureMaterializationPlan plan,
            Map<com.wayfinder.core.math.WorldPosition, PlannedBlockCell> blocks
    ) {
        for (int forward = -2; forward <= 2; forward++) {
            for (int right = -2; right <= 2; right++) {
                if (Math.abs(forward) == 2
                        && Math.abs(right) == 2) {
                    continue;
                }

                add(
                        plan,
                        blocks,
                        new LocalStructurePosition(
                                right,
                                -1,
                                forward
                        ),
                        BlockMaterialRole.FOUNDATION_STONE,
                        BlockFunction.FOUNDATION
                );
            }
        }
    }

    /**
     * Three-step rear approach.
     *
     * The player approaches from behind the node origin and is naturally
     * pulled onto the same line that continues toward the target.
     */
    private void approach(
            StructureMaterializationPlan plan,
            Map<com.wayfinder.core.math.WorldPosition, PlannedBlockCell> blocks
    ) {
        for (int forward = -5; forward <= -3; forward++) {
            add(
                    plan,
                    blocks,
                    new LocalStructurePosition(
                            0,
                            0,
                            forward
                    ),
                    BlockMaterialRole.PRIMARY_STONE,
                    BlockFunction.APPROACH
            );
        }
    }

    /**
     * The architectural clue.
     *
     * A five-cell spine runs directly through the Shrine toward the target.
     * Even if the glyph surface is damaged, this axis remains readable.
     */
    private void directionalAxis(
            StructureMaterializationPlan plan,
            Map<com.wayfinder.core.math.WorldPosition, PlannedBlockCell> blocks
    ) {
        for (int forward = -2; forward <= 2; forward++) {
            add(
                    plan,
                    blocks,
                    new LocalStructurePosition(
                            0,
                            0,
                            forward
                    ),
                    BlockMaterialRole.ACCENT_STONE,
                    BlockFunction.DIRECTION_AXIS
            );
        }

        /*
         * Forward terminal stone makes the axis asymmetrical. The Shrine has
         * a meaningful "toward" end rather than merely a line through it.
         */
        add(
                plan,
                blocks,
                new LocalStructurePosition(
                        0,
                        1,
                        2
                ),
                BlockMaterialRole.ACCENT_STONE,
                BlockFunction.DIRECTION_AXIS
        );
    }

    /**
     * Two side piers frame the axis but never close the forward view.
     */
    private void shrineBody(
            StructureMaterializationPlan plan,
            Map<com.wayfinder.core.math.WorldPosition, PlannedBlockCell> blocks
    ) {
        // Approved centered sanctuary wall with stepped upper silhouette.
        for (int right = -2; right <= 2; right++) {
            for (int up = 0; up <= 4; up++) {
                if (up == 4 && Math.abs(right) > 1) continue;
                add(plan, blocks,
                        new LocalStructurePosition(right, up, -1),
                        BlockMaterialRole.PRIMARY_STONE,
                        BlockFunction.SHRINE_BODY);
            }
        }

        // Twin pillars frame the clue while leaving the target side open.
        for (int right : new int[]{-3, 3}) {
            for (int up = 0; up <= 5; up++) {
                add(plan, blocks,
                        new LocalStructurePosition(right, up, 0),
                        BlockMaterialRole.PRIMARY_STONE,
                        BlockFunction.SHRINE_BODY);
            }
            add(plan, blocks,
                    new LocalStructurePosition(right, 0, 1),
                    BlockMaterialRole.PRIMARY_STONE,
                    BlockFunction.SHRINE_BODY);
        }
    }

    private void glyphSurface(
            StructureMaterializationPlan plan,
            Map<com.wayfinder.core.math.WorldPosition, PlannedBlockCell> blocks
    ) {
        for (var local : WayGlyphPattern.localCells()) {
            var world = transform.toWorld(
                    plan.intent().origin(),
                    plan.intent().facing(),
                    local);
            // Glyph replaces the wall cell; it is not a disconnected ornament.
            blocks.remove(world);
            add(plan, blocks, local,
                    BlockMaterialRole.GLYPH_STONE,
                    BlockFunction.GLYPH_SURFACE);
        }
    }

    private void add(
            StructureMaterializationPlan plan,
            Map<com.wayfinder.core.math.WorldPosition, PlannedBlockCell> blocks,
            LocalStructurePosition local,
            BlockMaterialRole material,
            BlockFunction function
    ) {
        var world =
                transform.toWorld(
                        plan.intent().origin(),
                        plan.intent().facing(),
                        local
                );

        blocks.putIfAbsent(
                world,
                new PlannedBlockCell(
                        world,
                        material,
                        function
                )
        );
    }
}

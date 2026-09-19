package com.wayfinder.structure.shrine;

import com.wayfinder.core.id.NodeId;
import com.wayfinder.core.math.WorldPosition;
import com.wayfinder.structure.geometry.BlockFunction;
import com.wayfinder.structure.model.HorizontalFacing;
import com.wayfinder.structure.model.PlannedStructureComponent;
import com.wayfinder.structure.model.StructureArchetype;
import com.wayfinder.structure.model.StructureComponentRole;
import com.wayfinder.structure.model.StructureIntent;
import com.wayfinder.structure.model.StructureMaterializationPlan;
import com.wayfinder.structure.model.StructurePurpose;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

final class WaystoneShrineGeometrySolverTest {

    @Test
    void shrineAxisPointsTowardSemanticTarget() {
        var plan =
                materializationPlan(
                        HorizontalFacing.EAST,
                        new WorldPosition(
                                100,
                                70,
                                100
                        ),
                        new WorldPosition(
                                180,
                                70,
                                100
                        )
                );

        var geometry =
                new WaystoneShrineGeometrySolver()
                        .solve(plan);

        assertTrue(
                geometry.blocks()
                        .stream()
                        .filter(block ->
                                block.function()
                                        == BlockFunction.DIRECTION_AXIS
                        )
                        .anyMatch(block ->
                                block.position()
                                        .equals(
                                                new WorldPosition(
                                                        102,
                                                        70,
                                                        100
                                                )
                                        )
                        )
        );

        assertTrue(
                geometry.blocks()
                        .stream()
                        .filter(block ->
                                block.function()
                                        == BlockFunction.APPROACH
                        )
                        .allMatch(block ->
                                block.position().x()
                                        < plan.intent()
                                                .origin()
                                                .x()
                        )
        );
    }

    @Test
    void shrineSupportsDiagonalOrientationWithoutCardinalizing() {
        var plan =
                materializationPlan(
                        HorizontalFacing.SOUTH_EAST,
                        new WorldPosition(
                                0,
                                70,
                                0
                        ),
                        new WorldPosition(
                                80,
                                70,
                                80
                        )
                );

        var geometry =
                new WaystoneShrineGeometrySolver()
                        .solve(plan);

        assertTrue(
                geometry.blocks()
                        .stream()
                        .filter(block ->
                                block.function()
                                        == BlockFunction.DIRECTION_AXIS
                        )
                        .anyMatch(block ->
                                block.position()
                                        .equals(
                                                new WorldPosition(
                                                        2,
                                                        70,
                                                        2
                                                )
                                        )
                        )
        );
    }

    @Test
    void glyphDoesNotCarryDirectionAlone() {
        var geometry =
                new WaystoneShrineGeometrySolver()
                        .solve(
                                materializationPlan(
                                        HorizontalFacing.NORTH,
                                        new WorldPosition(
                                                0,
                                                70,
                                                0
                                        ),
                                        new WorldPosition(
                                                0,
                                                70,
                                                -80
                                        )
                                )
                        );

        long axisBlocks =
                geometry.blocks()
                        .stream()
                        .filter(block ->
                                block.function()
                                        == BlockFunction.DIRECTION_AXIS
                        )
                        .count();

        long glyphBlocks =
                geometry.blocks()
                        .stream()
                        .filter(block ->
                                block.function()
                                        == BlockFunction.GLYPH_SURFACE
                        )
                        .count();

        assertTrue(axisBlocks >= 5);
        assertTrue(glyphBlocks >= 1);
        assertTrue(axisBlocks > glyphBlocks);
    }

    @Test
    void everyBlockHasOnePurposefulFunctionAndNoPositionsOverlap() {
        var geometry =
                new WaystoneShrineGeometrySolver()
                        .solve(
                                materializationPlan(
                                        HorizontalFacing.WEST,
                                        new WorldPosition(
                                                20,
                                                70,
                                                20
                                        ),
                                        new WorldPosition(
                                                -60,
                                                70,
                                                20
                                        )
                                )
                        );

        assertEquals(
                geometry.blocks().size(),
                geometry.blocks()
                        .stream()
                        .map(block ->
                                block.position()
                        )
                        .distinct()
                        .count()
        );

        assertTrue(
                geometry.blocks()
                        .stream()
                        .allMatch(block ->
                                block.function()
                                        != null
                        )
        );
    }

    @Test
    void dryRunSummaryExposesFunctionalComposition() {
        var geometry =
                new WaystoneShrineGeometrySolver()
                        .solve(
                                materializationPlan(
                                        HorizontalFacing.SOUTH,
                                        new WorldPosition(
                                                0,
                                                70,
                                                0
                                        ),
                                        new WorldPosition(
                                                0,
                                                70,
                                                80
                                        )
                                )
                        );

        var summary =
                WaystoneShrineDryRunSummary.from(
                        geometry
                );

        assertEquals(
                geometry.blocks().size(),
                summary.totalBlocks()
        );
        assertTrue(
                summary.blocksByFunction()
                        .containsKey(
                                BlockFunction.FOUNDATION
                        )
        );
        assertTrue(
                summary.blocksByFunction()
                        .containsKey(
                                BlockFunction.DIRECTION_AXIS
                        )
        );
        assertTrue(
                summary.blocksByFunction()
                        .containsKey(
                                BlockFunction.GLYPH_SURFACE
                        )
        );
    }

    private static StructureMaterializationPlan materializationPlan(
            HorizontalFacing facing,
            WorldPosition origin,
            WorldPosition target
    ) {
        StructureIntent intent =
                new StructureIntent(
                        new NodeId(
                                UUID.fromString(
                                        "11111111-1111-1111-1111-111111111111"
                                )
                        ),
                        StructureArchetype.WAYSTONE_SHRINE,
                        origin,
                        target,
                        facing,
                        Set.of(
                                StructurePurpose.COMMUNICATE_DIRECTION,
                                StructurePurpose.SUPPORT_APPROACH,
                                StructurePurpose.MARK_GLYPH_SURFACE,
                                StructurePurpose.ANCHOR_TO_TERRAIN
                        )
                );

        return new StructureMaterializationPlan(
                intent,
                List.of(
                        new PlannedStructureComponent(
                                StructureComponentRole.FOUNDATION,
                                Set.of(
                                        StructurePurpose.ANCHOR_TO_TERRAIN
                                )
                        ),
                        new PlannedStructureComponent(
                                StructureComponentRole.APPROACH,
                                Set.of(
                                        StructurePurpose.SUPPORT_APPROACH
                                )
                        ),
                        new PlannedStructureComponent(
                                StructureComponentRole.DIRECTION_AXIS,
                                Set.of(
                                        StructurePurpose.COMMUNICATE_DIRECTION
                                )
                        ),
                        new PlannedStructureComponent(
                                StructureComponentRole.GLYPH_SURFACE,
                                Set.of(
                                        StructurePurpose.MARK_GLYPH_SURFACE
                                )
                        )
                )
        );
    }
}

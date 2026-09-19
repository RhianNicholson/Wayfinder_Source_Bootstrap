package com.wayfinder.structure.placement;

import com.wayfinder.core.id.NodeId;
import com.wayfinder.core.math.WorldPosition;
import com.wayfinder.structure.geometry.BlockFunction;
import com.wayfinder.structure.geometry.BlockMaterialRole;
import com.wayfinder.structure.geometry.PlannedBlockCell;
import com.wayfinder.structure.geometry.StructureGeometryPlan;
import com.wayfinder.structure.model.*;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

final class WatchtowerFoundationPlacementGuardTest {

    @Test
    void matchingTowerFoundationAloneDoesNotCountAsHistoricalUpperStructure() {
        var foundation =
                new PlannedBlockCell(
                        new WorldPosition(0, 69, 0),
                        BlockMaterialRole.TOWER_FOUNDATION_STONE,
                        BlockFunction.TOWER_FOUNDATION
                );

        var support =
                new PlannedBlockCell(
                        new WorldPosition(0, 70, 0),
                        BlockMaterialRole.TOWER_SUPPORT_STONE,
                        BlockFunction.TOWER_SUPPORT
                );

        var geometry =
                new StructureGeometryPlan(
                        plan(),
                        List.of(
                                foundation,
                                support
                        )
                );

        var decision =
                new StructurePlacementGuard()
                        .evaluate(
                                geometry,
                                cell ->
                                        cell.function()
                                                        == BlockFunction.TOWER_FOUNDATION
                                                ? StructureCellState.MATCHING_PLANNED_BLOCK
                                                : StructureCellState.EMPTY
                        );

        assertEquals(
                StructurePlacementStatus.READY,
                decision.status()
        );
    }

    private static StructureMaterializationPlan plan() {
        var intent =
                new StructureIntent(
                        new NodeId(
                                UUID.fromString(
                                        "11111111-1111-1111-1111-111111111111"
                                )
                        ),
                        StructureArchetype.WATCHTOWER,
                        new WorldPosition(0, 70, 0),
                        new WorldPosition(100, 80, 0),
                        HorizontalFacing.EAST,
                        Set.of(
                                StructurePurpose.ENABLE_OBSERVATION
                        )
                );

        return new StructureMaterializationPlan(
                intent,
                List.of(
                        new PlannedStructureComponent(
                                StructureComponentRole.OBSERVATION_PLATFORM,
                                Set.of(
                                        StructurePurpose.ENABLE_OBSERVATION
                                )
                        )
                )
        );
    }
}

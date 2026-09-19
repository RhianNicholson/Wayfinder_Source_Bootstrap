package com.wayfinder.structure.placement;

import com.wayfinder.core.id.NodeId;
import com.wayfinder.core.math.WorldPosition;
import com.wayfinder.structure.geometry.BlockFunction;
import com.wayfinder.structure.geometry.BlockMaterialRole;
import com.wayfinder.structure.geometry.PlannedBlockCell;
import com.wayfinder.structure.geometry.StructureGeometryPlan;
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

final class StructurePlacementGuardTest {

    @Test
    void cleanSiteIsReady() {
        var decision = new StructurePlacementGuard()
                .evaluate(
                        geometry(),
                        cell -> cell.function() == BlockFunction.FOUNDATION
                                ? StructureCellState.REPLACEABLE_FOUNDATION
                                : StructureCellState.EMPTY
                );

        assertEquals(StructurePlacementStatus.READY, decision.status());
        assertTrue(decision.mayPlace());
    }

    @Test
    void exactExistingStructureIsIdempotentNoOp() {
        var decision = new StructurePlacementGuard()
                .evaluate(
                        geometry(),
                        cell -> StructureCellState.MATCHING_PLANNED_BLOCK
                );

        assertEquals(
                StructurePlacementStatus.ALREADY_PRESENT,
                decision.status()
        );
        assertFalse(decision.mayPlace());
    }

    @Test
    void partialUpperStructureIsNeverAutoRepaired() {
        WorldPosition existing = new WorldPosition(0, 70, 0);

        var decision = new StructurePlacementGuard()
                .evaluate(
                        geometry(),
                        cell -> {
                            if (cell.position().equals(existing)) {
                                return StructureCellState.MATCHING_PLANNED_BLOCK;
                            }

                            return cell.function() == BlockFunction.FOUNDATION
                                    ? StructureCellState.REPLACEABLE_FOUNDATION
                                    : StructureCellState.EMPTY;
                        }
                );

        assertEquals(
                StructurePlacementStatus.PARTIAL_EXISTING_STRUCTURE,
                decision.status()
        );
        assertFalse(decision.mayPlace());
    }

    @Test
    void blockedUpperCellRejectsPlacement() {
        var decision = new StructurePlacementGuard()
                .evaluate(
                        geometry(),
                        cell -> cell.function() == BlockFunction.GLYPH_SURFACE
                                ? StructureCellState.BLOCKED
                                : cell.function() == BlockFunction.FOUNDATION
                                ? StructureCellState.REPLACEABLE_FOUNDATION
                                : StructureCellState.EMPTY
                );

        assertEquals(StructurePlacementStatus.BLOCKED, decision.status());
        assertFalse(decision.mayPlace());
    }

    private static StructureGeometryPlan geometry() {
        StructureIntent intent =
                new StructureIntent(
                        new NodeId(
                                UUID.fromString(
                                        "11111111-1111-1111-1111-111111111111"
                                )
                        ),
                        StructureArchetype.WAYSTONE_SHRINE,
                        new WorldPosition(0, 70, 0),
                        new WorldPosition(80, 70, 0),
                        HorizontalFacing.EAST,
                        Set.of(
                                StructurePurpose.COMMUNICATE_DIRECTION
                        )
                );

        StructureMaterializationPlan materialization =
                new StructureMaterializationPlan(
                        intent,
                        List.of(
                                new PlannedStructureComponent(
                                        StructureComponentRole.DIRECTION_AXIS,
                                        Set.of(
                                                StructurePurpose.COMMUNICATE_DIRECTION
                                        )
                                )
                        )
                );

        return new StructureGeometryPlan(
                materialization,
                List.of(
                        new PlannedBlockCell(
                                new WorldPosition(0, 69, 0),
                                BlockMaterialRole.FOUNDATION_STONE,
                                BlockFunction.FOUNDATION
                        ),
                        new PlannedBlockCell(
                                new WorldPosition(0, 70, 0),
                                BlockMaterialRole.ACCENT_STONE,
                                BlockFunction.DIRECTION_AXIS
                        ),
                        new PlannedBlockCell(
                                new WorldPosition(1, 70, 0),
                                BlockMaterialRole.GLYPH_STONE,
                                BlockFunction.GLYPH_SURFACE
                        )
                )
        );
    }
}

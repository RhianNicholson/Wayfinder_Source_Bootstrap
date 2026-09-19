package com.wayfinder.structure.materialization;

import com.wayfinder.core.id.NodeId;
import com.wayfinder.core.math.WorldPosition;
import com.wayfinder.structure.geometry.BlockFunction;
import com.wayfinder.structure.geometry.BlockMaterialRole;
import com.wayfinder.structure.model.StructureArchetype;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

final class MaterializationInspectorTest {

    @Test
    void allOriginalCellsMatchingIsIntact() {
        var record = record();
        var inspection =
                new MaterializationInspector()
                        .inspect(
                                record,
                                (r, cell) -> true
                        );

        assertEquals(MaterializationCondition.INTACT, inspection.condition());
        assertEquals(2, inspection.matchingCells());
    }

    @Test
    void someOriginalCellsMatchingIsDamaged() {
        var record = record();
        var surviving =
                Set.of(new WorldPosition(0, 70, 0));

        var inspection =
                new MaterializationInspector()
                        .inspect(
                                record,
                                (r, cell) ->
                                        surviving.contains(cell.position())
                        );

        assertEquals(MaterializationCondition.DAMAGED, inspection.condition());
        assertEquals(1, inspection.matchingCells());
    }

    @Test
    void noOriginalCellsMatchingIsLost() {
        var record = record();
        var inspection =
                new MaterializationInspector()
                        .inspect(
                                record,
                                (r, cell) -> false
                        );

        assertEquals(MaterializationCondition.LOST, inspection.condition());
        assertEquals(0, inspection.matchingCells());
    }

    private MaterializationRecord record() {
        return new MaterializationRecord(
                new NodeId(
                        UUID.fromString(
                                "11111111-1111-1111-1111-111111111111"
                        )
                ),
                StructureArchetype.WATCHTOWER,
                1,
                1,
                MaterializationCondition.INTACT,
                List.of(
                        new MaterializedBlockCell(
                                new WorldPosition(0, 70, 0),
                                BlockMaterialRole.PLATFORM_STONE,
                                BlockFunction.OBSERVATION_PLATFORM
                        ),
                        new MaterializedBlockCell(
                                new WorldPosition(1, 70, 0),
                                BlockMaterialRole.VIEW_ACCENT_STONE,
                                BlockFunction.VIEW_FRAME
                        )
                )
        );
    }
}

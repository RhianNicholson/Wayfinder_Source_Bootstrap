package com.wayfinder.structure.materialization;

import com.wayfinder.core.id.NodeId;
import com.wayfinder.core.math.WorldPosition;
import com.wayfinder.structure.geometry.BlockFunction;
import com.wayfinder.structure.geometry.BlockMaterialRole;
import com.wayfinder.structure.model.StructureArchetype;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

final class MaterializationCommitServiceTest {
    private final NodeId nodeId = new NodeId(
            UUID.fromString("11111111-1111-1111-1111-111111111111")
    );

    @Test
    void firstPlacementBecomesPersistentTruth() {
        var service = new MaterializationCommitService();
        var record = record(MaterializationCondition.INTACT);

        var result = service.recordPlacement(MaterializationState.empty(), record);

        assertTrue(result.committed());
        assertEquals(record, result.state().find(nodeId).orElseThrow());
    }

    @Test
    void secondPlacementCannotRewriteOriginalGeometry() {
        var service = new MaterializationCommitService();
        var original = record(MaterializationCondition.INTACT);
        var first = service.recordPlacement(MaterializationState.empty(), original);

        var different = new MaterializationRecord(
                nodeId,
                StructureArchetype.WATCHTOWER,
                1,
                2,
                MaterializationCondition.INTACT,
                List.of(new MaterializedBlockCell(
                        new WorldPosition(99, 99, 99),
                        BlockMaterialRole.PLATFORM_STONE,
                        BlockFunction.OBSERVATION_PLATFORM
                ))
        );

        var second = service.recordPlacement(first.state(), different);

        assertFalse(second.committed());
        assertEquals(original, second.state().find(nodeId).orElseThrow());
    }

    @Test
    void damageChangesConditionButPreservesOriginalCells() {
        var service = new MaterializationCommitService();
        var original = record(MaterializationCondition.INTACT);
        var first = service.recordPlacement(MaterializationState.empty(), original);

        var damaged = service.updateCondition(
                first.state(),
                original,
                MaterializationCondition.DAMAGED
        );

        assertEquals(MaterializationCondition.DAMAGED, damaged.record().condition());
        assertEquals(original.originalCells(), damaged.record().originalCells());
    }

    private MaterializationRecord record(MaterializationCondition condition) {
        return new MaterializationRecord(
                nodeId,
                StructureArchetype.WATCHTOWER,
                1,
                1,
                condition,
                List.of(new MaterializedBlockCell(
                        new WorldPosition(0, 70, 0),
                        BlockMaterialRole.PLATFORM_STONE,
                        BlockFunction.OBSERVATION_PLATFORM
                ))
        );
    }
}

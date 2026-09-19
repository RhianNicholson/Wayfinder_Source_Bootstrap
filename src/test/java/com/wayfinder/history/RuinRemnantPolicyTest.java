package com.wayfinder.history;

import com.wayfinder.core.id.NodeId;
import com.wayfinder.core.math.WorldPosition;
import com.wayfinder.structure.geometry.BlockFunction;
import com.wayfinder.structure.geometry.BlockMaterialRole;
import com.wayfinder.structure.materialization.MaterializationCondition;
import com.wayfinder.structure.materialization.MaterializationRecord;
import com.wayfinder.structure.materialization.MaterializedBlockCell;
import com.wayfinder.structure.model.StructureArchetype;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

final class RuinRemnantPolicyTest {
    @Test
    void watchtowerLeavesOnlyFoundationAndLowestTwoSupportCourses() {
        var record = new MaterializationRecord(
                new NodeId(UUID.fromString("11111111-1111-1111-1111-111111111111")),
                StructureArchetype.WATCHTOWER, 1, 1, MaterializationCondition.INTACT,
                List.of(
                        cell(0, 69, 0, BlockFunction.TOWER_FOUNDATION),
                        cell(0, 70, 0, BlockFunction.TOWER_SUPPORT),
                        cell(0, 71, 0, BlockFunction.TOWER_SUPPORT),
                        cell(0, 72, 0, BlockFunction.TOWER_SUPPORT),
                        cell(0, 76, 0, BlockFunction.OBSERVATION_PLATFORM),
                        cell(0, 77, 0, BlockFunction.VIEW_FRAME)
                ));

        var survivors = new RuinRemnantPolicy().survivingCells(record);

        assertEquals(3, survivors.size());
        assertTrue(survivors.stream().anyMatch(c -> c.function()==BlockFunction.TOWER_FOUNDATION));
        assertEquals(2, survivors.stream().filter(c -> c.function()==BlockFunction.TOWER_SUPPORT).count());
        assertTrue(survivors.stream().noneMatch(c -> c.function()==BlockFunction.OBSERVATION_PLATFORM));
        assertTrue(survivors.stream().noneMatch(c -> c.function()==BlockFunction.VIEW_FRAME));
    }

    private static MaterializedBlockCell cell(int x,int y,int z,BlockFunction function) {
        return new MaterializedBlockCell(
                new WorldPosition(x,y,z),
                function==BlockFunction.TOWER_FOUNDATION
                        ? BlockMaterialRole.TOWER_FOUNDATION_STONE
                        : BlockMaterialRole.TOWER_SUPPORT_STONE,
                function);
    }
}

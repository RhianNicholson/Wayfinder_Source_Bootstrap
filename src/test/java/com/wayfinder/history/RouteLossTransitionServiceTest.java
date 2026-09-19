package com.wayfinder.history;

import com.wayfinder.core.id.NodeId;
import com.wayfinder.core.math.WorldPosition;
import com.wayfinder.structure.geometry.BlockFunction;
import com.wayfinder.structure.geometry.BlockMaterialRole;
import com.wayfinder.structure.materialization.*;
import com.wayfinder.structure.model.StructureArchetype;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

final class RouteLossTransitionServiceTest {

    private final NodeId nodeId =
            new NodeId(
                    UUID.fromString(
                            "11111111-1111-1111-1111-111111111111"
                    )
            );

    @Test
    void recordedIntactStructureMayBeLost() {
        var materialization =
                new MaterializationState(
                        List.of(record(MaterializationCondition.INTACT))
                );

        var validation =
                new RouteLossTransitionService()
                        .validate(
                                HistoricalEventState.empty(),
                                materialization,
                                nodeId
                        );

        assertTrue(validation.valid());
        assertEquals("VALID", validation.reason());
    }

    @Test
    void missingMaterializationCannotCreateLossHistory() {
        var validation =
                new RouteLossTransitionService()
                        .validate(
                                HistoricalEventState.empty(),
                                MaterializationState.empty(),
                                nodeId
                        );

        assertFalse(validation.valid());
        assertEquals(
                "NO_MATERIALIZATION_RECORD",
                validation.reason()
        );
    }

    @Test
    void alreadyLostStructureCannotBeLostAgain() {
        var materialization =
                new MaterializationState(
                        List.of(record(MaterializationCondition.LOST))
                );

        var validation =
                new RouteLossTransitionService()
                        .validate(
                                HistoricalEventState.empty(),
                                materialization,
                                nodeId
                        );

        assertFalse(validation.valid());
        assertEquals(
                "ALREADY_LOST",
                validation.reason()
        );
    }

    @Test
    void committedRouteLossCannotBeAppliedTwice() {
        var materialization =
                new MaterializationState(
                        List.of(record(MaterializationCondition.INTACT))
                );

        var factory = new HistoricalEventFactory();
        var event =
                factory.routeLoss(
                        HistoricalEventState.empty(),
                        nodeId,
                        "historical route loss"
                );

        var committed =
                new HistoricalEventCommitService()
                        .commit(
                                HistoricalEventState.empty(),
                                event
                        );

        var validation =
                new RouteLossTransitionService()
                        .validate(
                                committed.state(),
                                materialization,
                                nodeId
                        );

        assertFalse(validation.valid());
        assertEquals(
                "EVENT_ALREADY_COMMITTED",
                validation.reason()
        );
    }

    @Test
    void markingLostPreservesOriginalGeometry() {
        var original = record(MaterializationCondition.INTACT);
        var state =
                new MaterializationState(
                        List.of(original)
                );

        var lost =
                new RouteLossTransitionService()
                        .markLost(
                                state,
                                original
                        );

        var result =
                lost.find(nodeId).orElseThrow();

        assertEquals(
                MaterializationCondition.LOST,
                result.condition()
        );
        assertEquals(
                original.originalCells(),
                result.originalCells()
        );
    }

    private MaterializationRecord record(
            MaterializationCondition condition
    ) {
        return new MaterializationRecord(
                nodeId,
                StructureArchetype.WATCHTOWER,
                1,
                1,
                condition,
                List.of(
                        new MaterializedBlockCell(
                                new WorldPosition(0, 70, 0),
                                BlockMaterialRole.PLATFORM_STONE,
                                BlockFunction.OBSERVATION_PLATFORM
                        )
                )
        );
    }
}

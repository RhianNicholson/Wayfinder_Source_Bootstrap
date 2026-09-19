package com.wayfinder.structure.watchtower;

import com.wayfinder.core.id.NodeId;
import com.wayfinder.core.math.WorldPosition;
import com.wayfinder.geography.world.WorldTerrainView;
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

final class WatchtowerGeometrySolverTest {

    @Test
    void flatTerrainUsesMinimumFunctionalHeight() {
        var result =
                solver().solve(
                        plan(),
                        terrain(
                                (x, z) -> 70
                        )
                ).orElseThrow();

        assertEquals(
                6,
                result.sightline()
                        .towerHeight()
        );
        assertTrue(
                result.geometry()
                        .blocks()
                        .stream()
                        .anyMatch(block ->
                                block.function()
                                        == BlockFunction.OBSERVATION_PLATFORM
                        )
        );
    }

    @Test
    void interveningRidgeForcesHigherPlatform() {
        var result =
                solver().solve(
                        plan(),
                        terrain(
                                (x, z) ->
                                        x >= 30 && x <= 50
                                                ? 80
                                                : 70
                        )
                ).orElseThrow();

        assertTrue(
                result.sightline()
                        .towerHeight()
                        > 6
        );
    }

    @Test
    void impossibleSightlineProducesNoTower() {
        var result =
                new WatchtowerGeometrySolver(
                        new WatchtowerSightlineSolver(
                                32,
                                6,
                                10,
                                0.75
                        )
                ).solve(
                        plan(),
                        terrain(
                                (x, z) ->
                                        x >= 15 && x <= 70
                                                ? 100
                                                : 70
                        )
                );

        assertTrue(result.isEmpty());
    }

    @Test
    void primaryViewArcRemainsOpenTowardTarget() {
        var result =
                solver().solve(
                        plan(),
                        terrain(
                                (x, z) -> 70
                        )
                ).orElseThrow();

        int platformUp =
                result.sightline()
                        .towerHeight();

        /*
         * Facing EAST means local forward +2 maps to world x +2.
         * Front-center at railing height must remain empty.
         */
        WorldPosition openViewCell =
                new WorldPosition(
                        2,
                        70 + platformUp + 1,
                        0
                );

        assertTrue(
                result.geometry()
                        .blocks()
                        .stream()
                        .noneMatch(block ->
                                block.position()
                                        .equals(
                                                openViewCell
                                        )
                        )
        );
    }

    @Test
    void supportsAreDerivedDownwardFromPlatform() {
        var result =
                solver().solve(
                        plan(),
                        terrain(
                                (x, z) -> 70
                        )
                ).orElseThrow();

        long supports =
                result.geometry()
                        .blocks()
                        .stream()
                        .filter(block ->
                                block.function()
                                        == BlockFunction.TOWER_SUPPORT
                        )
                        .count();

        assertEquals(
                result.sightline()
                        .towerHeight()
                        * 4L,
                supports
        );
    }

    private static WatchtowerGeometrySolver solver() {
        return new WatchtowerGeometrySolver(
                new WatchtowerSightlineSolver(
                        32,
                        6,
                        24,
                        0.75
                )
        );
    }

    private static StructureMaterializationPlan plan() {
        StructureIntent intent =
                new StructureIntent(
                        new NodeId(
                                UUID.fromString(
                                        "11111111-1111-1111-1111-111111111111"
                                )
                        ),
                        StructureArchetype.WATCHTOWER,
                        new WorldPosition(
                                0,
                                70,
                                0
                        ),
                        new WorldPosition(
                                100,
                                82,
                                0
                        ),
                        HorizontalFacing.EAST,
                        Set.of(
                                StructurePurpose.ENABLE_OBSERVATION,
                                StructurePurpose.PROTECT_VIEW_ARC,
                                StructurePurpose.SUPPORT_PLATFORM,
                                StructurePurpose.ANCHOR_TO_TERRAIN
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
                        ),
                        new PlannedStructureComponent(
                                StructureComponentRole.VIEW_ARC,
                                Set.of(
                                        StructurePurpose.PROTECT_VIEW_ARC
                                )
                        ),
                        new PlannedStructureComponent(
                                StructureComponentRole.TOWER_SUPPORT,
                                Set.of(
                                        StructurePurpose.SUPPORT_PLATFORM
                                )
                        ),
                        new PlannedStructureComponent(
                                StructureComponentRole.FOUNDATION,
                                Set.of(
                                        StructurePurpose.ANCHOR_TO_TERRAIN
                                )
                        )
                )
        );
    }

    private static WorldTerrainView terrain(
            java.util.function.BiFunction<Integer, Integer, Integer> height
    ) {
        return new WorldTerrainView() {
            @Override
            public int surfaceHeight(
                    int x,
                    int z
            ) {
                return height.apply(x, z);
            }

            @Override
            public boolean isWater(
                    int x,
                    int y,
                    int z
            ) {
                return false;
            }

            @Override
            public boolean isSolid(
                    int x,
                    int y,
                    int z
            ) {
                return y < surfaceHeight(x, z);
            }
        };
    }
}

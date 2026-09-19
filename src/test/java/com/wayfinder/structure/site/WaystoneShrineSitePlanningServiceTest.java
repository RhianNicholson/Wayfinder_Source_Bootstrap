package com.wayfinder.structure.site;

import com.wayfinder.core.id.NodeId;
import com.wayfinder.core.math.WorldPosition;
import com.wayfinder.geography.world.WorldTerrainView;
import com.wayfinder.structure.model.HorizontalFacing;
import com.wayfinder.structure.model.PlannedStructureComponent;
import com.wayfinder.structure.model.StructureArchetype;
import com.wayfinder.structure.model.StructureComponentRole;
import com.wayfinder.structure.model.StructureIntent;
import com.wayfinder.structure.model.StructureMaterializationPlan;
import com.wayfinder.structure.model.StructurePurpose;
import com.wayfinder.structure.shrine.WaystoneShrineGeometrySolver;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.function.BiFunction;

import static org.junit.jupiter.api.Assertions.*;

final class WaystoneShrineSitePlanningServiceTest {

    @Test
    void flatDrySiteIsAcceptedWithoutExtraFoundation() {
        var result =
                service().evaluate(
                        geometry(),
                        terrain(
                                (x, z) -> 70,
                                false
                        )
                );

        assertTrue(result.validation().accepted());
        assertEquals(
                0,
                result.adapted()
                        .orElseThrow()
                        .addedFoundationBlocks()
        );
    }

    @Test
    void shallowDropExtendsFoundationWithoutMovingShrine() {
        var original =
                geometry();

        var result =
                service().evaluate(
                        original,
                        terrain(
                                (x, z) ->
                                        x == 0 && z == 0
                                                ? 70
                                                : 68,
                                false
                        )
                );

        var adapted =
                result.adapted()
                        .orElseThrow();

        assertTrue(
                adapted.addedFoundationBlocks()
                        > 0
        );

        assertEquals(
                original.materializationPlan()
                        .intent()
                        .origin(),
                adapted.geometry()
                        .materializationPlan()
                        .intent()
                        .origin()
        );

        assertEquals(
                original.materializationPlan()
                        .intent()
                        .facing(),
                adapted.geometry()
                        .materializationPlan()
                        .intent()
                        .facing()
        );
    }

    @Test
    void steepDropRejectsInsteadOfRewritingHistory() {
        var result =
                service().evaluate(
                        geometry(),
                        terrain(
                                (x, z) ->
                                        x < 0 ? 64 : 70,
                                false
                        )
                );

        assertFalse(
                result.validation()
                        .accepted()
        );
        assertTrue(
                result.validation()
                        .issues()
                        .stream()
                        .anyMatch(issue ->
                                issue.code()
                                        == ShrineSiteIssueCode.FOUNDATION_DROP_TOO_DEEP
                                        || issue.code()
                                        == ShrineSiteIssueCode.TERRAIN_TOO_UNEVEN
                        )
        );
        assertTrue(
                result.adapted()
                        .isEmpty()
        );
    }

    @Test
    void waterRejectsSite() {
        var result =
                service().evaluate(
                        geometry(),
                        terrain(
                                (x, z) -> 70,
                                true
                        )
                );

        assertFalse(
                result.validation()
                        .accepted()
        );
        assertTrue(
                result.validation()
                        .issues()
                        .stream()
                        .anyMatch(issue ->
                                issue.code()
                                        == ShrineSiteIssueCode.WATER_IN_FOOTPRINT
                        )
        );
    }

    private static WaystoneShrineSitePlanningService service() {
        return new WaystoneShrineSitePlanningService(
                new WaystoneShrineSiteValidator(
                        3,
                        1,
                        3
                ),
                new WaystoneShrineTerrainAdapter()
        );
    }

    private static com.wayfinder.structure.geometry.StructureGeometryPlan geometry() {
        StructureIntent intent =
                new StructureIntent(
                        new NodeId(
                                UUID.fromString(
                                        "11111111-1111-1111-1111-111111111111"
                                )
                        ),
                        StructureArchetype.WAYSTONE_SHRINE,
                        new WorldPosition(
                                0,
                                70,
                                0
                        ),
                        new WorldPosition(
                                80,
                                70,
                                0
                        ),
                        HorizontalFacing.EAST,
                        Set.of(
                                StructurePurpose.COMMUNICATE_DIRECTION,
                                StructurePurpose.SUPPORT_APPROACH,
                                StructurePurpose.MARK_GLYPH_SURFACE,
                                StructurePurpose.ANCHOR_TO_TERRAIN
                        )
                );

        StructureMaterializationPlan materialization =
                new StructureMaterializationPlan(
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

        return new WaystoneShrineGeometrySolver()
                .solve(
                        materialization
                );
    }

    private static WorldTerrainView terrain(
            BiFunction<Integer, Integer, Integer> height,
            boolean water
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
                return water && y == surfaceHeight(x, z) - 1;
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

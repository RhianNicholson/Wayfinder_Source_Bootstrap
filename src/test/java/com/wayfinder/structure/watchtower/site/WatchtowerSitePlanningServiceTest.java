package com.wayfinder.structure.watchtower.site;

import com.wayfinder.core.id.NodeId;
import com.wayfinder.core.math.WorldPosition;
import com.wayfinder.geography.world.WorldTerrainView;
import com.wayfinder.structure.model.*;
import com.wayfinder.structure.watchtower.*;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.function.BiFunction;

import static org.junit.jupiter.api.Assertions.*;

final class WatchtowerSitePlanningServiceTest {

    @Test
    void flatSiteIsAcceptedWithoutChangingPlatform() {
        var world = terrain((x, z) -> 70, false);
        var tower = geometry(world);

        var result = service().evaluate(tower, world);

        assertTrue(result.validation().accepted());
        assertEquals(
                tower.sightline().platformY(),
                result.adapted().orElseThrow().sightline().platformY()
        );
    }

    @Test
    void shallowSupportDropExtendsDownward() {
        var solveWorld = terrain((x, z) -> 70, false);
        var tower = geometry(solveWorld);
        var actual = terrain(
                (x, z) -> (x == -2 && z == -2) ? 68 : 70,
                false
        );

        var result = service().evaluate(tower, actual);

        assertTrue(result.validation().accepted());
        assertTrue(result.adapted().orElseThrow().addedSupportBlocks() > 0);
        assertEquals(
                tower.sightline().towerHeight(),
                result.adapted().orElseThrow().sightline().towerHeight()
        );
    }

    @Test
    void deepSupportDropRejectsInsteadOfMovingTower() {
        var solveWorld = terrain((x, z) -> 70, false);
        var tower = geometry(solveWorld);
        var actual = terrain(
                (x, z) -> (x == -2 && z == -2) ? 62 : 70,
                false
        );

        var result = service().evaluate(tower, actual);

        assertFalse(result.validation().accepted());
        assertTrue(result.adapted().isEmpty());
        assertTrue(result.validation().issues().stream().anyMatch(
                i -> i.code() == WatchtowerSiteIssueCode.SUPPORT_DROP_TOO_DEEP
        ));
    }

    @Test
    void waterAtSupportRejects() {
        var world = terrain((x, z) -> 70, true);
        var tower = geometry(terrain((x, z) -> 70, false));

        var result = service().evaluate(tower, world);

        assertFalse(result.validation().accepted());
        assertTrue(result.validation().issues().stream().anyMatch(
                i -> i.code() == WatchtowerSiteIssueCode.WATER_AT_SUPPORT
        ));
    }

    @Test
    void newlyRequiredHigherSightlineRejectsOldPlatform() {
        var flat = terrain((x, z) -> 70, false);
        var tower = geometry(flat);

        var ridge = terrain(
                (x, z) -> x >= 30 && x <= 50 ? 80 : 70,
                false
        );

        var result = service().evaluate(tower, ridge);

        assertFalse(result.validation().accepted());
        assertTrue(result.validation().issues().stream().anyMatch(
                i -> i.code() == WatchtowerSiteIssueCode.SIGHTLINE_LOST
        ));
    }

    private static WatchtowerSitePlanningService service() {
        return new WatchtowerSitePlanningService(
                new WatchtowerSiteValidator(
                        3,
                        new WatchtowerSightlineSolver(32, 6, 24, 0.75)
                ),
                new WatchtowerTerrainAdapter()
        );
    }

    private static WatchtowerGeometryPlan geometry(WorldTerrainView world) {
        var intent = new StructureIntent(
                new NodeId(UUID.fromString(
                        "11111111-1111-1111-1111-111111111111"
                )),
                StructureArchetype.WATCHTOWER,
                new WorldPosition(0, 70, 0),
                new WorldPosition(100, 82, 0),
                HorizontalFacing.EAST,
                Set.of(
                        StructurePurpose.ENABLE_OBSERVATION,
                        StructurePurpose.PROTECT_VIEW_ARC,
                        StructurePurpose.SUPPORT_PLATFORM,
                        StructurePurpose.ANCHOR_TO_TERRAIN
                )
        );

        var plan = new StructureMaterializationPlan(
                intent,
                List.of(
                        new PlannedStructureComponent(
                                StructureComponentRole.OBSERVATION_PLATFORM,
                                Set.of(StructurePurpose.ENABLE_OBSERVATION)
                        ),
                        new PlannedStructureComponent(
                                StructureComponentRole.VIEW_ARC,
                                Set.of(StructurePurpose.PROTECT_VIEW_ARC)
                        ),
                        new PlannedStructureComponent(
                                StructureComponentRole.TOWER_SUPPORT,
                                Set.of(StructurePurpose.SUPPORT_PLATFORM)
                        ),
                        new PlannedStructureComponent(
                                StructureComponentRole.FOUNDATION,
                                Set.of(StructurePurpose.ANCHOR_TO_TERRAIN)
                        )
                )
        );

        return new WatchtowerGeometrySolver(
                new WatchtowerSightlineSolver(32, 6, 24, 0.75)
        ).solve(plan, world).orElseThrow();
    }

    private static WorldTerrainView terrain(
            BiFunction<Integer, Integer, Integer> height,
            boolean water
    ) {
        return new WorldTerrainView() {
            @Override
            public int surfaceHeight(int x, int z) {
                return height.apply(x, z);
            }

            @Override
            public boolean isWater(int x, int y, int z) {
                return water && y == surfaceHeight(x, z) - 1;
            }

            @Override
            public boolean isSolid(int x, int y, int z) {
                return y < surfaceHeight(x, z);
            }
        };
    }
}

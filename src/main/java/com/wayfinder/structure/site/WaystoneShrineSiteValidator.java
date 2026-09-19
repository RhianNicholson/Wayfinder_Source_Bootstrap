package com.wayfinder.structure.site;

import com.wayfinder.geography.world.WorldTerrainView;
import com.wayfinder.structure.geometry.BlockFunction;
import com.wayfinder.structure.geometry.StructureGeometryPlan;
import com.wayfinder.structure.model.StructureArchetype;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Validates whether historical Shrine geometry can plausibly inhabit the
 * actual terrain without moving or rotating the civilization node.
 */
public final class WaystoneShrineSiteValidator {
    private final int maximumTerrainSpread;
    private final int maximumRiseAboveBase;
    private final int maximumFoundationDrop;

    public WaystoneShrineSiteValidator(
            int maximumTerrainSpread,
            int maximumRiseAboveBase,
            int maximumFoundationDrop
    ) {
        this.maximumTerrainSpread = maximumTerrainSpread;
        this.maximumRiseAboveBase = maximumRiseAboveBase;
        this.maximumFoundationDrop = maximumFoundationDrop;
    }

    public ShrineSiteValidationResult validate(
            StructureGeometryPlan geometry,
            WorldTerrainView world
    ) {
        if (geometry.materializationPlan()
                .intent()
                .archetype()
                != StructureArchetype.WAYSTONE_SHRINE) {
            throw new IllegalArgumentException(
                    "Shrine site validator requires WAYSTONE_SHRINE geometry"
            );
        }

        int originY =
                geometry.materializationPlan()
                        .intent()
                        .origin()
                        .y();

        int baseSurfaceY = originY - 1;

        Set<Long> footprint =
                new HashSet<>();

        for (var block : geometry.blocks()) {
            footprint.add(key(
                    block.position().x(),
                    block.position().z()
            ));
        }

        int minSurface = Integer.MAX_VALUE;
        int maxSurface = Integer.MIN_VALUE;
        int deepestDrop = 0;
        boolean water = false;

        for (long packed : footprint) {
            int x = unpackX(packed);
            int z = unpackZ(packed);
            int surfaceY =
                    world.surfaceHeight(x, z) - 1;

            minSurface = Math.min(
                    minSurface,
                    surfaceY
            );
            maxSurface = Math.max(
                    maxSurface,
                    surfaceY
            );

            deepestDrop = Math.max(
                    deepestDrop,
                    baseSurfaceY - surfaceY
            );

            if (world.isWater(
                    x,
                    surfaceY,
                    z
            )) {
                water = true;
            }
        }

        List<ShrineSiteIssue> issues =
                new ArrayList<>();

        if (maxSurface - minSurface
                > maximumTerrainSpread) {
            issues.add(
                    new ShrineSiteIssue(
                            ShrineSiteIssueCode.TERRAIN_TOO_UNEVEN,
                            "Shrine footprint exceeds allowed terrain spread."
                    )
            );
        }

        if (maxSurface - baseSurfaceY
                > maximumRiseAboveBase) {
            issues.add(
                    new ShrineSiteIssue(
                            ShrineSiteIssueCode.TERRAIN_RISE_INTERSECTS_STRUCTURE,
                            "Terrain rises too far above the historical Shrine base."
                    )
            );
        }

        if (deepestDrop
                > maximumFoundationDrop) {
            issues.add(
                    new ShrineSiteIssue(
                            ShrineSiteIssueCode.FOUNDATION_DROP_TOO_DEEP,
                            "Shrine would require an implausibly deep artificial foundation."
                    )
            );
        }

        if (water) {
            issues.add(
                    new ShrineSiteIssue(
                            ShrineSiteIssueCode.WATER_IN_FOOTPRINT,
                            "Shrine footprint intersects water."
                    )
            );
        }

        boolean occupiedSpace =
                geometry.blocks()
                        .stream()
                        .filter(block ->
                                block.function()
                                        != BlockFunction.FOUNDATION
                        )
                        .anyMatch(block ->
                                world.isSolid(
                                        block.position().x(),
                                        block.position().y(),
                                        block.position().z()
                                )
                        );

        if (occupiedSpace) {
            issues.add(
                    new ShrineSiteIssue(
                            ShrineSiteIssueCode.OCCUPIED_STRUCTURE_SPACE,
                            "Existing terrain occupies required Shrine structure space."
                    )
            );
        }

        return new ShrineSiteValidationResult(
                issues.isEmpty(),
                minSurface,
                maxSurface,
                deepestDrop,
                issues
        );
    }

    private static long key(
            int x,
            int z
    ) {
        return ((long) x << 32)
                ^ (z & 0xffffffffL);
    }

    private static int unpackX(long value) {
        return (int) (value >> 32);
    }

    private static int unpackZ(long value) {
        return (int) value;
    }
}

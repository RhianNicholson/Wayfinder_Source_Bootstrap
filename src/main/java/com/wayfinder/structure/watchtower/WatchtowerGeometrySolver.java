package com.wayfinder.structure.watchtower;

import com.wayfinder.core.math.WorldPosition;
import com.wayfinder.geography.world.WorldTerrainView;
import com.wayfinder.structure.geometry.BlockFunction;
import com.wayfinder.structure.geometry.BlockMaterialRole;
import com.wayfinder.structure.geometry.LocalStructurePosition;
import com.wayfinder.structure.geometry.PlannedBlockCell;
import com.wayfinder.structure.geometry.StructureCoordinateTransform;
import com.wayfinder.structure.geometry.StructureGeometryPlan;
import com.wayfinder.structure.model.StructureArchetype;
import com.wayfinder.structure.model.StructureMaterializationPlan;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * First physical Watchtower solver.
 *
 * The platform is solved first from the sightline requirement. Supports and
 * foundation are then generated downward from that functional platform.
 */
public final class WatchtowerGeometrySolver {
    private final WatchtowerSightlineSolver sightlineSolver;
    private final StructureCoordinateTransform transform =
            new StructureCoordinateTransform();

    public WatchtowerGeometrySolver(
            WatchtowerSightlineSolver sightlineSolver
    ) {
        this.sightlineSolver = sightlineSolver;
    }

    public java.util.Optional<WatchtowerGeometryPlan> solve(
            StructureMaterializationPlan plan,
            WorldTerrainView world
    ) {
        if (plan.intent().archetype()
                != StructureArchetype.WATCHTOWER) {
            throw new IllegalArgumentException(
                    "WatchtowerGeometrySolver requires a WATCHTOWER plan"
            );
        }

        WatchtowerSightlineResult sightline =
                sightlineSolver.solve(
                        plan.intent().origin(),
                        plan.intent().semanticTarget(),
                        world
                );

        if (!sightline.solved()) {
            return java.util.Optional.empty();
        }

        Map<WorldPosition, PlannedBlockCell> blocks =
                new LinkedHashMap<>();

        /*
         * PLATFORM FIRST.
         *
         * Local forward is the historical view direction. The front-center
         * edge remains open so the landmark is visually privileged.
         */
        int platformUp = sightline.towerHeight();

        for (int forward = -2; forward <= 2; forward++) {
            for (int right = -2; right <= 2; right++) {
                add(
                        plan,
                        blocks,
                        new LocalStructurePosition(
                                right,
                                platformUp,
                                forward
                        ),
                        BlockMaterialRole.PLATFORM_STONE,
                        BlockFunction.OBSERVATION_PLATFORM
                );
            }
        }

        /*
         * Low frame around three sides. The target-facing center is left open
         * to protect the primary view arc.
         */
        for (int right = -2; right <= 2; right++) {
            add(
                    plan,
                    blocks,
                    new LocalStructurePosition(
                            right,
                            platformUp + 1,
                            -2
                    ),
                    BlockMaterialRole.VIEW_ACCENT_STONE,
                    BlockFunction.VIEW_FRAME
            );
        }

        for (int forward = -1; forward <= 2; forward++) {
            add(
                    plan,
                    blocks,
                    new LocalStructurePosition(
                            -2,
                            platformUp + 1,
                            forward
                    ),
                    BlockMaterialRole.VIEW_ACCENT_STONE,
                    BlockFunction.VIEW_FRAME
            );
            add(
                    plan,
                    blocks,
                    new LocalStructurePosition(
                            2,
                            platformUp + 1,
                            forward
                    ),
                    BlockMaterialRole.VIEW_ACCENT_STONE,
                    BlockFunction.VIEW_FRAME
            );
        }

        /*
         * Canonical △ SIGHT glyph.
         *
         * The glyph is placed on the rear frame, opposite the protected
         * target-facing opening. It therefore labels the observational
         * relationship without blocking the sightline it helps the player
         * understand.
         */
        for (var local : SightGlyphPattern.localCells(platformUp)) {
            add(
                    plan,
                    blocks,
                    local,
                    BlockMaterialRole.VIEW_ACCENT_STONE,
                    BlockFunction.VIEW_FRAME
            );
        }

        /*
         * STRUCTURE DOWNWARD.
         *
         * Four supports descend from the platform corners. Their required
         * height exists only because the solved platform requires it.
         */
        for (int right : new int[]{-2, 2}) {
            for (int forward : new int[]{-2, 2}) {
                for (int up = 0; up < platformUp; up++) {
                    add(
                            plan,
                            blocks,
                            new LocalStructurePosition(
                                    right,
                                    up,
                                    forward
                            ),
                            BlockMaterialRole.TOWER_SUPPORT_STONE,
                            BlockFunction.TOWER_SUPPORT
                    );
                }

                add(
                        plan,
                        blocks,
                        new LocalStructurePosition(
                                right,
                                -1,
                                forward
                        ),
                        BlockMaterialRole.TOWER_FOUNDATION_STONE,
                        BlockFunction.TOWER_FOUNDATION
                );
            }
        }

        return java.util.Optional.of(
                new WatchtowerGeometryPlan(
                        new StructureGeometryPlan(
                                plan,
                                List.copyOf(
                                        blocks.values()
                                )
                        ),
                        sightline
                )
        );
    }

    private void add(
            StructureMaterializationPlan plan,
            Map<WorldPosition, PlannedBlockCell> blocks,
            LocalStructurePosition local,
            BlockMaterialRole material,
            BlockFunction function
    ) {
        WorldPosition world =
                transform.toWorld(
                        plan.intent().origin(),
                        plan.intent().facing(),
                        local
                );

        blocks.putIfAbsent(
                world,
                new PlannedBlockCell(
                        world,
                        material,
                        function
                )
        );
    }
}

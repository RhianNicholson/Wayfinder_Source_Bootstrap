package com.wayfinder.structure.service;

import com.wayfinder.structure.model.PlannedStructureComponent;
import com.wayfinder.structure.model.StructureComponentRole;
import com.wayfinder.structure.model.StructureIntent;
import com.wayfinder.structure.model.StructureMaterializationPlan;
import com.wayfinder.structure.model.StructurePurpose;

import java.util.List;
import java.util.Set;

/**
 * Turns semantic intent into a purposeful physical contract.
 *
 * It still does not know what a Minecraft block is.
 */
public final class StructureMaterializationPlanner {

    public StructureMaterializationPlan plan(
            StructureIntent intent
    ) {
        return switch (intent.archetype()) {
            case WAYSTONE_SHRINE ->
                    shrine(intent);
            case WATCHTOWER ->
                    watchtower(intent);
        };
    }

    private StructureMaterializationPlan shrine(
            StructureIntent intent
    ) {
        return new StructureMaterializationPlan(
                intent,
                List.of(
                        component(
                                StructureComponentRole.FOUNDATION,
                                StructurePurpose.ANCHOR_TO_TERRAIN
                        ),
                        component(
                                StructureComponentRole.APPROACH,
                                StructurePurpose.SUPPORT_APPROACH
                        ),
                        component(
                                StructureComponentRole.DIRECTION_AXIS,
                                StructurePurpose.COMMUNICATE_DIRECTION
                        ),
                        component(
                                StructureComponentRole.GLYPH_SURFACE,
                                StructurePurpose.MARK_GLYPH_SURFACE,
                                StructurePurpose.COMMUNICATE_DIRECTION
                        )
                )
        );
    }

    private StructureMaterializationPlan watchtower(
            StructureIntent intent
    ) {
        return new StructureMaterializationPlan(
                intent,
                List.of(
                        component(
                                StructureComponentRole.OBSERVATION_PLATFORM,
                                StructurePurpose.ENABLE_OBSERVATION
                        ),
                        component(
                                StructureComponentRole.VIEW_ARC,
                                StructurePurpose.PROTECT_VIEW_ARC,
                                StructurePurpose.ENABLE_OBSERVATION
                        ),
                        component(
                                StructureComponentRole.TOWER_SUPPORT,
                                StructurePurpose.SUPPORT_PLATFORM
                        ),
                        component(
                                StructureComponentRole.FOUNDATION,
                                StructurePurpose.ANCHOR_TO_TERRAIN
                        )
                )
        );
    }

    private PlannedStructureComponent component(
            StructureComponentRole role,
            StructurePurpose... purposes
    ) {
        return new PlannedStructureComponent(
                role,
                Set.of(purposes)
        );
    }
}

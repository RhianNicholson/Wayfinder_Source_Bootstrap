package com.wayfinder.structure.service;

import com.wayfinder.civilization.model.NodePurpose;
import com.wayfinder.civilization.relationship.RelationshipType;
import com.wayfinder.civilization.state.CivilizationNode;
import com.wayfinder.civilization.state.CivilizationState;
import com.wayfinder.structure.model.HorizontalFacing;
import com.wayfinder.structure.model.StructureArchetype;
import com.wayfinder.structure.model.StructureIntent;
import com.wayfinder.structure.model.StructurePurpose;

import java.util.Optional;
import java.util.Set;

/**
 * Civilization truth -> structure intent.
 *
 * No terrain writes, Minecraft blocks, templates, or decoration are allowed in
 * this layer.
 */
public final class StructureIntentFactory {

    public Optional<StructureIntent> create(
            CivilizationNode node,
            CivilizationState state
    ) {
        return switch (node.purpose()) {
            case DIRECTION -> directionIntent(
                    node,
                    state
            );
            case OBSERVATION -> observationIntent(
                    node
            );
            default -> Optional.empty();
        };
    }

    private Optional<StructureIntent> directionIntent(
            CivilizationNode node,
            CivilizationState state
    ) {
        var targetId =
                node.civilizationTarget();

        if (targetId.isEmpty()) {
            return Optional.empty();
        }

        var destination =
                state.nodes()
                        .stream()
                        .filter(candidate ->
                                candidate.id()
                                        .equals(
                                                targetId.get()
                                        )
                        )
                        .filter(candidate ->
                                candidate.purpose()
                                        == NodePurpose.OBSERVATION
                        )
                        .findFirst();

        if (destination.isEmpty()) {
            return Optional.empty();
        }

        boolean committedRelationshipExists =
                state.relationships()
                        .stream()
                        .anyMatch(relationship ->
                                relationship.type()
                                        == RelationshipType.DIRECTIONAL_REFERENCE
                                && relationship.sourceNodeId()
                                        .equals(
                                                node.id()
                                        )
                                && relationship.targetNodeId()
                                        .equals(
                                                destination.get()
                                                        .id()
                                        )
                        );

        /*
         * Empty space is a valid answer. A DIRECTION node without its
         * committed relationship is not enough evidence to materialize a
         * shrine.
         */
        if (!committedRelationshipExists) {
            return Optional.empty();
        }

        return Optional.of(
                new StructureIntent(
                        node.id(),
                        StructureArchetype.WAYSTONE_SHRINE,
                        node.position(),
                        destination.get()
                                .position(),
                        HorizontalFacing.toward(
                                node.position(),
                                destination.get()
                                        .position()
                        ),
                        Set.of(
                                StructurePurpose.COMMUNICATE_DIRECTION,
                                StructurePurpose.SUPPORT_APPROACH,
                                StructurePurpose.MARK_GLYPH_SURFACE,
                                StructurePurpose.ANCHOR_TO_TERRAIN
                        )
                )
        );
    }

    private Optional<StructureIntent> observationIntent(
            CivilizationNode node
    ) {
        var landmark =
                node.geographicTarget();

        if (landmark.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(
                new StructureIntent(
                        node.id(),
                        StructureArchetype.WATCHTOWER,
                        node.position(),
                        landmark.get()
                                .anchor(),
                        HorizontalFacing.toward(
                                node.position(),
                                landmark.get()
                                        .anchor()
                        ),
                        Set.of(
                                StructurePurpose.ENABLE_OBSERVATION,
                                StructurePurpose.PROTECT_VIEW_ARC,
                                StructurePurpose.SUPPORT_PLATFORM,
                                StructurePurpose.ANCHOR_TO_TERRAIN
                        )
                )
        );
    }
}

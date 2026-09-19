package com.wayfinder.structure.service;

import com.wayfinder.civilization.model.NodePurpose;
import com.wayfinder.civilization.reason.ReasonEvaluation;
import com.wayfinder.civilization.reason.ReasonRecord;
import com.wayfinder.civilization.reason.ReasonType;
import com.wayfinder.civilization.relationship.CivilizationRelationship;
import com.wayfinder.civilization.relationship.RelationshipType;
import com.wayfinder.civilization.state.CivilizationNode;
import com.wayfinder.civilization.state.CivilizationState;
import com.wayfinder.civilization.target.CivilizationNodeTarget;
import com.wayfinder.core.id.GeographicFeatureId;
import com.wayfinder.core.id.NodeId;
import com.wayfinder.core.id.RelationshipId;
import com.wayfinder.core.math.WorldPosition;
import com.wayfinder.geography.model.Landmark;
import com.wayfinder.geography.model.LandmarkType;
import com.wayfinder.structure.model.HorizontalFacing;
import com.wayfinder.structure.model.StructureArchetype;
import com.wayfinder.structure.model.StructureComponentRole;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

final class CivilizationStructurePlanningServiceTest {

    @Test
    void committedDirectionRelationshipCreatesShrineFacingObservation() {
        CivilizationNode observation =
                observation();
        CivilizationNode direction =
                direction(
                        observation.id()
                );

        CivilizationState state =
                new CivilizationState(
                        List.of(
                                observation,
                                direction
                        ),
                        List.of(
                                relationship(
                                        direction,
                                        observation
                                )
                        )
                );

        var plans =
                service().plan(state);

        var shrine =
                plans.stream()
                        .filter(plan ->
                                plan.intent()
                                        .archetype()
                                        == StructureArchetype.WAYSTONE_SHRINE
                        )
                        .findFirst()
                        .orElseThrow();

        assertEquals(
                direction.position(),
                shrine.intent().origin()
        );
        assertEquals(
                observation.position(),
                shrine.intent()
                        .semanticTarget()
        );
        assertEquals(
                HorizontalFacing.EAST,
                shrine.intent().facing()
        );
        assertTrue(
                shrine.components()
                        .stream()
                        .anyMatch(component ->
                                component.role()
                                        == StructureComponentRole.DIRECTION_AXIS
                        )
        );
    }

    @Test
    void directionWithoutCommittedRelationshipDoesNotCreateShrine() {
        CivilizationNode observation =
                observation();
        CivilizationNode direction =
                direction(
                        observation.id()
                );

        CivilizationState state =
                new CivilizationState(
                        List.of(
                                observation,
                                direction
                        )
                );

        var plans =
                service().plan(state);

        assertTrue(
                plans.stream()
                        .noneMatch(plan ->
                                plan.intent()
                                        .archetype()
                                        == StructureArchetype.WAYSTONE_SHRINE
                        )
        );
    }

    @Test
    void observationCreatesWatchtowerFacingLandmark() {
        CivilizationNode observation =
                observation();

        var plans =
                service().plan(
                        new CivilizationState(
                                List.of(observation)
                        )
                );

        var tower =
                plans.stream()
                        .filter(plan ->
                                plan.intent()
                                        .archetype()
                                        == StructureArchetype.WATCHTOWER
                        )
                        .findFirst()
                        .orElseThrow();

        assertEquals(
                landmark().anchor(),
                tower.intent()
                        .semanticTarget()
        );
        assertEquals(
                HorizontalFacing.EAST,
                tower.intent().facing()
        );
        assertTrue(
                tower.components()
                        .stream()
                        .anyMatch(component ->
                                component.role()
                                        == StructureComponentRole.OBSERVATION_PLATFORM
                        )
        );
        assertTrue(
                tower.components()
                        .stream()
                        .anyMatch(component ->
                                component.role()
                                        == StructureComponentRole.VIEW_ARC
                        )
        );
    }

    @Test
    void everyPlannedComponentHasAnExplicitPurpose() {
        CivilizationNode observation =
                observation();
        CivilizationNode direction =
                direction(
                        observation.id()
                );

        CivilizationState state =
                new CivilizationState(
                        List.of(
                                observation,
                                direction
                        ),
                        List.of(
                                relationship(
                                        direction,
                                        observation
                                )
                        )
                );

        assertTrue(
                service().plan(state)
                        .stream()
                        .flatMap(plan ->
                                plan.components()
                                        .stream()
                        )
                        .allMatch(component ->
                                !component.purposes()
                                        .isEmpty()
                        )
        );
    }

    private static CivilizationStructurePlanningService service() {
        return new CivilizationStructurePlanningService(
                new StructureIntentFactory(),
                new StructureMaterializationPlanner()
        );
    }

    private static CivilizationNode observation() {
        return new CivilizationNode(
                new NodeId(
                        UUID.fromString(
                                "11111111-1111-1111-1111-111111111111"
                        )
                ),
                NodePurpose.OBSERVATION,
                new WorldPosition(
                        0,
                        70,
                        0
                ),
                landmark(),
                0.84,
                reasons(
                        ReasonType.OBSERVATION
                ),
                "OBS:structure-test"
        );
    }

    private static CivilizationNode direction(
            NodeId observationId
    ) {
        return new CivilizationNode(
                new NodeId(
                        UUID.fromString(
                                "22222222-2222-2222-2222-222222222222"
                        )
                ),
                NodePurpose.DIRECTION,
                new WorldPosition(
                        -80,
                        70,
                        0
                ),
                new CivilizationNodeTarget(
                        observationId
                ),
                0.82,
                reasons(
                        ReasonType.COMMUNICATION
                ),
                "DIR:structure-test"
        );
    }

    private static CivilizationRelationship relationship(
            CivilizationNode source,
            CivilizationNode target
    ) {
        return new CivilizationRelationship(
                new RelationshipId(
                        UUID.fromString(
                                "44444444-4444-4444-4444-444444444444"
                        )
                ),
                RelationshipType.DIRECTIONAL_REFERENCE,
                source.id(),
                target.id(),
                0.82,
                reasons(
                        ReasonType.COMMUNICATION
                ),
                "DREF:structure-test"
        );
    }

    private static Landmark landmark() {
        return new Landmark(
                new GeographicFeatureId(
                        UUID.fromString(
                                "33333333-3333-3333-3333-333333333333"
                        )
                ),
                new WorldPosition(
                        160,
                        100,
                        0
                ),
                LandmarkType.PROMINENT_PEAK,
                0.90,
                0.85,
                0.75
        );
    }

    private static ReasonEvaluation reasons(
            ReasonType primary
    ) {
        return new ReasonEvaluation(
                new ReasonRecord(
                        primary,
                        0.90,
                        "Purposeful test reason."
                ),
                List.of()
        );
    }
}

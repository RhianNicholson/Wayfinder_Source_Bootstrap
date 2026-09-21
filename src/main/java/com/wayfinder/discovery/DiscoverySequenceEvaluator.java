package com.wayfinder.discovery;

import com.wayfinder.civilization.model.NodePurpose;
import com.wayfinder.civilization.relationship.RelationshipType;
import com.wayfinder.civilization.state.CivilizationState;
import com.wayfinder.history.HistoricalEventState;
import com.wayfinder.history.HistoricalEventType;
import com.wayfinder.structure.materialization.MaterializationCondition;
import com.wayfinder.structure.materialization.MaterializationState;

import java.util.EnumSet;
import java.util.Set;

public final class DiscoverySequenceEvaluator {
    public DiscoverySequenceResult evaluate(
            CivilizationState civilization,
            MaterializationState materialization,
            HistoricalEventState history
    ) {
        Set<DiscoveryBeat> beats = EnumSet.noneOf(DiscoveryBeat.class);

        var directionNodes = civilization.nodes().stream()
                .filter(n -> n.purpose() == NodePurpose.DIRECTION).toList();
        var observationNodes = civilization.nodes().stream()
                .filter(n -> n.purpose() == NodePurpose.OBSERVATION).toList();

        if (directionNodes.stream().anyMatch(n -> materialization.find(n.id())
                .map(r -> r.condition() != MaterializationCondition.LOST).orElse(false)))
            beats.add(DiscoveryBeat.WAY_SHRINE_PRESENT);

        if (civilization.relationships().stream()
                .anyMatch(r -> r.type() == RelationshipType.DIRECTIONAL_REFERENCE))
            beats.add(DiscoveryBeat.DIRECTIONAL_RELATIONSHIP_PRESENT);

        if (observationNodes.stream().anyMatch(n -> materialization.find(n.id())
                .map(r -> r.condition() == MaterializationCondition.INTACT).orElse(false)))
            beats.add(DiscoveryBeat.SIGHT_TOWER_PRESENT);

        if (observationNodes.stream().anyMatch(n -> n.geographicTarget().isPresent()))
            beats.add(DiscoveryBeat.LANDMARK_TARGET_PRESENT);

        if (history.events().stream()
                .filter(e -> e.type() == HistoricalEventType.ROUTE_LOSS)
                .anyMatch(e -> materialization.find(e.affectedNodeId())
                        .map(r -> r.condition() != MaterializationCondition.INTACT)
                        .orElse(false)))
            beats.add(DiscoveryBeat.BROKEN_CONTINUATION_PRESENT);

        boolean coherent =
                beats.contains(DiscoveryBeat.WAY_SHRINE_PRESENT)
                && beats.contains(DiscoveryBeat.DIRECTIONAL_RELATIONSHIP_PRESENT)
                && beats.contains(DiscoveryBeat.LANDMARK_TARGET_PRESENT)
                && (beats.contains(DiscoveryBeat.SIGHT_TOWER_PRESENT)
                    || beats.contains(DiscoveryBeat.BROKEN_CONTINUATION_PRESENT));

        return new DiscoverySequenceResult(Set.copyOf(beats), coherent);
    }
}

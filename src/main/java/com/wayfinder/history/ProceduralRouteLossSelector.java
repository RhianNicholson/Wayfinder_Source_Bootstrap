package com.wayfinder.history;

import com.wayfinder.civilization.model.NodePurpose;
import com.wayfinder.civilization.relationship.RelationshipType;
import com.wayfinder.civilization.state.CivilizationState;
import com.wayfinder.structure.materialization.MaterializationCondition;
import com.wayfinder.structure.materialization.MaterializationState;

import java.util.Comparator;
import java.util.List;

/**
 * Finds committed directional continuations eligible for historical route loss.
 *
 * When a HistoricalRegionKey is supplied, both ends of the continuation must
 * belong to that region. Cross-region history therefore cannot accidentally
 * mutate another region's civilization truth.
 */
public final class ProceduralRouteLossSelector {

    public List<RouteLossCandidate> candidates(
            CivilizationState civilization,
            MaterializationState materialization,
            HistoricalEventState history
    ) {
        return candidates(civilization, materialization, history, null);
    }

    public List<RouteLossCandidate> candidates(
            CivilizationState civilization,
            MaterializationState materialization,
            HistoricalEventState history,
            HistoricalRegionKey region
    ) {
        return civilization.relationships().stream()
                .filter(r -> r.type() == RelationshipType.DIRECTIONAL_REFERENCE)
                .flatMap(r -> {
                    var source = civilization.nodes().stream()
                            .filter(n -> n.id().equals(r.sourceNodeId()))
                            .findFirst();
                    var target = civilization.nodes().stream()
                            .filter(n -> n.id().equals(r.targetNodeId()))
                            .findFirst();

                    if (source.isEmpty() || target.isEmpty())
                        return java.util.stream.Stream.empty();
                    if (source.get().purpose() != NodePurpose.DIRECTION
                            || target.get().purpose() != NodePurpose.OBSERVATION)
                        return java.util.stream.Stream.empty();

                    if (region != null) {
                        var sourceRegion = HistoricalRegionKey.from(source.get().position());
                        var targetRegion = HistoricalRegionKey.from(target.get().position());
                        if (!region.equals(sourceRegion) || !region.equals(targetRegion))
                            return java.util.stream.Stream.empty();
                    }

                    var record = materialization.find(target.get().id());
                    if (record.isEmpty()
                            || record.get().condition() == MaterializationCondition.LOST)
                        return java.util.stream.Stream.empty();

                    boolean alreadyLost = history.events().stream()
                            .anyMatch(e -> e.type() == HistoricalEventType.ROUTE_LOSS
                                    && e.affectedNodeId().equals(target.get().id()));
                    if (alreadyLost)
                        return java.util.stream.Stream.empty();

                    double distance = source.get().position()
                            .horizontalDistanceTo(target.get().position());
                    double distanceFactor = Math.min(1.0, distance / 144.0);
                    double vulnerability = 0.35 + (distanceFactor * 0.40);

                    return java.util.stream.Stream.of(new RouteLossCandidate(
                            source.get().id(),
                            target.get().id(),
                            vulnerability,
                            "committed directional continuation exposed to historical loss"));
                })
                .sorted(Comparator
                        .comparingDouble(RouteLossCandidate::vulnerability)
                        .reversed()
                        .thenComparing(c -> c.destinationNodeId().value().toString()))
                .toList();
    }
}

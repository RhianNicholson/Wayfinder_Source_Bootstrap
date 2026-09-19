package com.wayfinder.history;

import com.wayfinder.civilization.relationship.RelationshipType;
import com.wayfinder.civilization.state.CivilizationState;
import com.wayfinder.structure.materialization.MaterializationCondition;
import com.wayfinder.structure.materialization.MaterializationState;

import java.util.Comparator;
import java.util.Optional;

public final class BrokenContinuationSelector {

    public Optional<BrokenContinuation> select(
            CivilizationState civilization,
            MaterializationState materialization
    ) {
        return civilization.relationships()
                .stream()
                .filter(relationship ->
                        relationship.type()
                                == RelationshipType.DIRECTIONAL_REFERENCE
                )
                .sorted(
                        Comparator.comparing(
                                relationship ->
                                        relationship.id().value().toString()
                        )
                )
                .map(relationship -> {
                    var source = civilization.nodes().stream()
                            .filter(node -> node.id().equals(
                                    relationship.sourceNodeId()))
                            .findFirst();

                    var target = civilization.nodes().stream()
                            .filter(node -> node.id().equals(
                                    relationship.targetNodeId()))
                            .findFirst();

                    var record = materialization.find(
                            relationship.targetNodeId());

                    if (source.isEmpty()
                            || target.isEmpty()
                            || record.isEmpty()
                            || record.get().condition()
                                    == MaterializationCondition.LOST) {
                        return Optional.<BrokenContinuation>empty();
                    }

                    return Optional.of(
                            new BrokenContinuation(
                                    source.get(),
                                    relationship,
                                    target.get(),
                                    record.get()
                            )
                    );
                })
                .flatMap(Optional::stream)
                .findFirst();
    }
}

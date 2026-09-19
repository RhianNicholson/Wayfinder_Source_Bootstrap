package com.wayfinder.structure.service;

import com.wayfinder.civilization.state.CivilizationState;
import com.wayfinder.structure.model.StructureMaterializationPlan;

import java.util.Comparator;
import java.util.List;

/**
 * Read-only projection of committed civilization truth into physical plans.
 */
public final class CivilizationStructurePlanningService {
    private final StructureIntentFactory intentFactory;
    private final StructureMaterializationPlanner planner;

    public CivilizationStructurePlanningService(
            StructureIntentFactory intentFactory,
            StructureMaterializationPlanner planner
    ) {
        this.intentFactory =
                intentFactory;
        this.planner =
                planner;
    }

    public List<StructureMaterializationPlan> plan(
            CivilizationState state
    ) {
        return state.nodes()
                .stream()
                .map(node ->
                        intentFactory.create(
                                node,
                                state
                        )
                )
                .flatMap(Optional -> Optional.stream())
                .map(planner::plan)
                .sorted(
                        Comparator.comparing(
                                plan ->
                                        plan.intent()
                                                .sourceNodeId()
                                                .value()
                                                .toString()
                        )
                )
                .toList();
    }
}

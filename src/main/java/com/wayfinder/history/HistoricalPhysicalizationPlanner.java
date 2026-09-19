package com.wayfinder.history;

import com.wayfinder.structure.materialization.MaterializationCondition;
import com.wayfinder.structure.materialization.MaterializationState;

/**
 * Recovery-safe bridge from committed historical truth to physical work.
 *
 * History is authoritative. If ROUTE_LOSS exists while its materialization is
 * not yet LOST, the physical consequence still needs to be applied.
 */
public final class HistoricalPhysicalizationPlanner {

    public HistoricalPhysicalizationResult plan(
            HistoricalEventState history,
            MaterializationState materialization
    ) {
        for (var event : history.events()) {
            if (event.type() != HistoricalEventType.ROUTE_LOSS) {
                continue;
            }

            var record = materialization.find(event.affectedNodeId());
            if (record.isPresent()
                    && record.get().condition() != MaterializationCondition.LOST) {
                return HistoricalPhysicalizationResult.required(
                        event.affectedNodeId());
            }
        }

        return HistoricalPhysicalizationResult.none();
    }
}

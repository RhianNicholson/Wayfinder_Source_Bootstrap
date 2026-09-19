package com.wayfinder.history;

import com.wayfinder.core.id.NodeId;
import com.wayfinder.structure.materialization.MaterializationCommitService;
import com.wayfinder.structure.materialization.MaterializationCondition;
import com.wayfinder.structure.materialization.MaterializationRecord;
import com.wayfinder.structure.materialization.MaterializationState;

public final class RouteLossTransitionService {

    public Validation validate(
            HistoricalEventState historicalState,
            MaterializationState materializationState,
            NodeId targetNodeId
    ) {
        var record = materializationState.find(targetNodeId);

        if (record.isEmpty()) {
            return new Validation(false, null, "NO_MATERIALIZATION_RECORD");
        }

        if (record.get().condition() == MaterializationCondition.LOST) {
            return new Validation(false, record.get(), "ALREADY_LOST");
        }

        var event =
                new HistoricalEventFactory()
                        .routeLoss(
                                historicalState,
                                targetNodeId,
                                "historical route loss"
                        );

        if (historicalState.find(event.id()).isPresent()) {
            return new Validation(false, record.get(), "EVENT_ALREADY_COMMITTED");
        }

        return new Validation(true, record.get(), "VALID");
    }

    public MaterializationState markLost(
            MaterializationState current,
            MaterializationRecord record
    ) {
        var result =
                new MaterializationCommitService()
                        .updateCondition(
                                current,
                                record,
                                MaterializationCondition.LOST
                        );

        return result.state();
    }

    public record Validation(
            boolean valid,
            MaterializationRecord record,
            String reason
    ) {}
}

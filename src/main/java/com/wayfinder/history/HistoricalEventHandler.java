package com.wayfinder.history;
import com.wayfinder.structure.materialization.MaterializationState;
public interface HistoricalEventHandler {
 HistoricalEventType type();
 HistoricalEventHandlerResult handle(HistoricalEventOpportunity opportunity, HistoricalScope scope, MaterializationState materialization, HistoricalEventState history);
}

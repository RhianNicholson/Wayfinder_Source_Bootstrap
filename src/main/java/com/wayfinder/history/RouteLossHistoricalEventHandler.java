package com.wayfinder.history;
import com.wayfinder.structure.materialization.MaterializationState;
import java.util.Optional;
public final class RouteLossHistoricalEventHandler implements HistoricalEventHandler {
 private final RouteLossTransitionService validation;
 private final HistoricalEventFactory events;
 private final HistoricalEventCommitService commits;
 public RouteLossHistoricalEventHandler(RouteLossTransitionService validation, HistoricalEventFactory events, HistoricalEventCommitService commits) {
  this.validation=validation; this.events=events; this.commits=commits;
 }
 @Override public HistoricalEventType type(){ return HistoricalEventType.ROUTE_LOSS; }
 @Override public HistoricalEventHandlerResult handle(HistoricalEventOpportunity opportunity, HistoricalScope scope, MaterializationState materialization, HistoricalEventState history) {
  if(opportunity.type()!=HistoricalEventType.ROUTE_LOSS) return HistoricalEventHandlerResult.unchanged(history);
  var validated=validation.validate(history,materialization,opportunity.affectedNodeId());
  if(!validated.valid()) return HistoricalEventHandlerResult.unchanged(history);
  var event=events.routeLoss(history,opportunity.affectedNodeId(),"procedural route loss ["+scope.eraKey()+"]");
  var committed=commits.commit(history,event);
  if(!committed.committed()) return HistoricalEventHandlerResult.unchanged(history);
  return new HistoricalEventHandlerResult(committed.state(),Optional.of(event));
 }
}

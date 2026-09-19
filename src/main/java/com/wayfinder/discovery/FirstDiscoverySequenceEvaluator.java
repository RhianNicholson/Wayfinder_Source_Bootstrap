package com.wayfinder.discovery;

import com.wayfinder.civilization.model.NodePurpose;
import com.wayfinder.civilization.relationship.RelationshipType;
import com.wayfinder.civilization.state.CivilizationState;
import com.wayfinder.history.HistoricalEventState;
import com.wayfinder.history.HistoricalEventType;
import com.wayfinder.structure.materialization.MaterializationCondition;
import com.wayfinder.structure.materialization.MaterializationState;
import com.wayfinder.structure.model.StructureArchetype;
import java.util.ArrayList;

public final class FirstDiscoverySequenceEvaluator {
 public DiscoverySequenceAssessment assess(CivilizationState civilization,
        MaterializationState materialization, HistoricalEventState history) {
  var beats = new ArrayList<DiscoveryBeat>();
  boolean shrine = materialization.records().stream().anyMatch(r ->
      r.archetype()==StructureArchetype.WAYSTONE_SHRINE &&
      r.condition()!=MaterializationCondition.LOST);
  if(shrine) beats.add(DiscoveryBeat.WAY_SHRINE_PRESENT);
  boolean directional = civilization.relationships().stream().anyMatch(r ->
      r.type()==RelationshipType.DIRECTIONAL_REFERENCE);
  if(directional) beats.add(DiscoveryBeat.DIRECTIONAL_RELATIONSHIP_PRESENT);
  boolean tower = materialization.records().stream().anyMatch(r ->
      r.archetype()==StructureArchetype.WATCHTOWER &&
      r.condition()!=MaterializationCondition.LOST);
  if(tower) beats.add(DiscoveryBeat.SIGHT_TOWER_PRESENT);
  boolean landmark = civilization.nodes().stream()
      .filter(n -> n.purpose()==NodePurpose.OBSERVATION)
      .anyMatch(n -> n.geographicTarget().isPresent());
  if(landmark) beats.add(DiscoveryBeat.LANDMARK_TARGET_PRESENT);
  boolean broken = history.events().stream()
      .filter(e -> e.type()==HistoricalEventType.ROUTE_LOSS)
      .anyMatch(e -> materialization.find(e.affectedNodeId())
          .map(r -> r.condition()==MaterializationCondition.LOST).orElse(false));
  if(broken) beats.add(DiscoveryBeat.BROKEN_CONTINUATION_PRESENT);
  boolean coherent = shrine && directional && landmark && (tower || broken);
  String summary = !coherent ? "First discovery sequence is incomplete."
      : broken ? "WAY clue and relationship survive while destination evidence is historically lost."
      : "WAY clue leads through committed relationship to SIGHT evidence and landmark.";
  return new DiscoverySequenceAssessment(beats,coherent,summary);
 }
}

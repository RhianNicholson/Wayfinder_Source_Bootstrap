package com.wayfinder.history;

import com.wayfinder.civilization.relationship.CivilizationRelationship;
import com.wayfinder.civilization.state.CivilizationNode;
import com.wayfinder.structure.materialization.MaterializationRecord;

public record BrokenContinuation(
        CivilizationNode sourceNode,
        CivilizationRelationship relationship,
        CivilizationNode lostTargetNode,
        MaterializationRecord targetMaterialization
) {}

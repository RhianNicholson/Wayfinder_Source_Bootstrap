package com.wayfinder.civilization.state;

import com.wayfinder.civilization.relationship.CivilizationRelationship;

import java.util.List;

public record CivilizationState(
        List<CivilizationNode> nodes,
        List<CivilizationRelationship> relationships
) {
    public CivilizationState {
        nodes = List.copyOf(nodes);
        relationships = List.copyOf(relationships);
    }

    /**
     * Backward-compatible constructor retained for Milestone 8/9 call sites.
     */
    public CivilizationState(List<CivilizationNode> nodes) {
        this(nodes, List.of());
    }

    public static CivilizationState empty() {
        return new CivilizationState(List.of(), List.of());
    }
}

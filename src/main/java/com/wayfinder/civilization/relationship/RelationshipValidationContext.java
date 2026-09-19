package com.wayfinder.civilization.relationship;

import com.wayfinder.civilization.state.CivilizationNode;

import java.util.List;

public record RelationshipValidationContext(
        List<CivilizationNode> nodes,
        List<CivilizationRelationship> relationships
) {
    public RelationshipValidationContext {
        nodes = List.copyOf(nodes);
        relationships = List.copyOf(relationships);
    }

    public static RelationshipValidationContext ofNodes(
            List<CivilizationNode> nodes
    ) {
        return new RelationshipValidationContext(nodes, List.of());
    }
}

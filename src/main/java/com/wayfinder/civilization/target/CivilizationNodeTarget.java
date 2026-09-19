package com.wayfinder.civilization.target;

import com.wayfinder.core.id.NodeId;

public record CivilizationNodeTarget(
        NodeId nodeId
) implements NodeTarget {
    public CivilizationNodeTarget {
        if (nodeId == null) {
            throw new IllegalArgumentException("nodeId is required");
        }
    }
}

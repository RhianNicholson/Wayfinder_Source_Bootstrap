package com.wayfinder.civilization.network;

import com.wayfinder.civilization.model.NodePurpose;
import com.wayfinder.core.id.NodeId;
import com.wayfinder.core.math.WorldPosition;

public record CommittedNodeSnapshot(
        NodeId id,
        NodePurpose purpose,
        WorldPosition position
) {}

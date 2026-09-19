package com.wayfinder.civilization.network;

import java.util.List;

public record NetworkValidationContext(
        List<CommittedNodeSnapshot> existingNodes
) {
    public NetworkValidationContext {
        existingNodes = List.copyOf(existingNodes);
    }

    public static NetworkValidationContext empty() {
        return new NetworkValidationContext(List.of());
    }
}

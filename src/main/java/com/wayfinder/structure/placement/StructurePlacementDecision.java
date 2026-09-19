package com.wayfinder.structure.placement;

public record StructurePlacementDecision(
        StructurePlacementStatus status,
        int matchingBlocks,
        int emptyBlocks,
        int replaceableFoundationBlocks,
        int blockedBlocks
) {
    public boolean mayPlace() {
        return status == StructurePlacementStatus.READY;
    }
}

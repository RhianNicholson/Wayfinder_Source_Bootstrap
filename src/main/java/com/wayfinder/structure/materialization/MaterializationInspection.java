package com.wayfinder.structure.materialization;

public record MaterializationInspection(
        MaterializationCondition condition,
        int matchingCells,
        int totalCells
) {
    public MaterializationInspection {
        if (condition == null) {
            throw new IllegalArgumentException("condition is required");
        }
        if (matchingCells < 0 || totalCells < 1 || matchingCells > totalCells) {
            throw new IllegalArgumentException("invalid materialization counts");
        }
    }

    public double integrity() {
        return (double) matchingCells / (double) totalCells;
    }
}

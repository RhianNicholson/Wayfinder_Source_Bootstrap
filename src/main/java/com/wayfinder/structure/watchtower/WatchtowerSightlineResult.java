package com.wayfinder.structure.watchtower;

public record WatchtowerSightlineResult(
        boolean solved,
        int platformY,
        int towerHeight,
        double visibilityScore
) {}

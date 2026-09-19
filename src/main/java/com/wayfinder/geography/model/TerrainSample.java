package com.wayfinder.geography.model;

import com.wayfinder.core.math.HorizontalPosition;

public record TerrainSample(
    HorizontalPosition position,
    int surfaceY,
    boolean water,
    double slope,
    double roughness
) {}

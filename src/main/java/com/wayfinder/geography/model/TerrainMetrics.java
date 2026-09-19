package com.wayfinder.geography.model;

public record TerrainMetrics(
    double meanElevation,
    double elevationVariance,
    double ruggedness
) {}

package com.wayfinder.geography.model;

import com.wayfinder.core.math.WorldPosition;

/** A geographic opportunity from which a landmark may be meaningfully observed. */
public record ObservationSite(
    WorldPosition position,
    Landmark target,
    double visibilityPotential,
    double buildability,
    double score
) {}

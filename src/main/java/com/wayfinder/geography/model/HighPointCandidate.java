package com.wayfinder.geography.model;

import com.wayfinder.core.math.WorldPosition;

/** A terrain-derived local maximum. It is an opportunity, not a civilization decision. */
public record HighPointCandidate(
    WorldPosition position,
    double prominence,
    double isolation,
    double slope
) {}

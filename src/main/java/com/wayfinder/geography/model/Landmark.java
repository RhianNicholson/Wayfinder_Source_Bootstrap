package com.wayfinder.geography.model;

import com.wayfinder.core.id.GeographicFeatureId;
import com.wayfinder.core.math.WorldPosition;

/** A salient geographic feature that can matter to a Wayfinder relationship. */
public record Landmark(
    GeographicFeatureId id,
    WorldPosition anchor,
    LandmarkType type,
    double salience,
    double prominence,
    double isolation
) {}

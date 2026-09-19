package com.wayfinder.civilization.target;

import com.wayfinder.geography.model.Landmark;

public record GeographicNodeTarget(
        Landmark landmark
) implements NodeTarget {
    public GeographicNodeTarget {
        if (landmark == null) {
            throw new IllegalArgumentException("landmark is required");
        }
    }
}

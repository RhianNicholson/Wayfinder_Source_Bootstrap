package com.wayfinder.core.id;

import java.util.Objects;
import java.util.UUID;

public record GeographicFeatureId(UUID value) {
    public GeographicFeatureId {
        Objects.requireNonNull(value, "value");
    }
}

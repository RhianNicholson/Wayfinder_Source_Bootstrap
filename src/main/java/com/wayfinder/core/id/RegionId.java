package com.wayfinder.core.id;

import java.util.Objects;
import java.util.UUID;

public record RegionId(UUID value) {
    public RegionId {
        Objects.requireNonNull(value, "value");
    }
}

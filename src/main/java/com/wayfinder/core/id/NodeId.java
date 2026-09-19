package com.wayfinder.core.id;

import java.util.Objects;
import java.util.UUID;

public record NodeId(UUID value) {
    public NodeId {
        Objects.requireNonNull(value, "value");
    }
}

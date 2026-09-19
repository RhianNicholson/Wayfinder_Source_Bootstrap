package com.wayfinder.core.id;

import java.util.Objects;
import java.util.UUID;

public record EventId(UUID value) {
    public EventId {
        Objects.requireNonNull(value, "value");
    }
}

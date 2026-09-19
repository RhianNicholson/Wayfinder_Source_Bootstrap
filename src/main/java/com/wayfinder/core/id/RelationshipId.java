package com.wayfinder.core.id;

import java.util.Objects;
import java.util.UUID;

public record RelationshipId(UUID value) {
    public RelationshipId {
        Objects.requireNonNull(value, "value");
    }
}

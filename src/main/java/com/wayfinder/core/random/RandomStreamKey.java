package com.wayfinder.core.random;

import java.util.Objects;

public record RandomStreamKey(
    long worldSeed,
    String regionKey,
    String stage,
    String scope,
    int generationVersion
) {
    public RandomStreamKey {
        Objects.requireNonNull(regionKey, "regionKey");
        Objects.requireNonNull(stage, "stage");
        Objects.requireNonNull(scope, "scope");
    }
}

package com.wayfinder.core.random;

import java.util.Objects;

public final class DefaultRandomStreamFactory implements RandomStreamFactory {
    private final SeedDeriver seedDeriver;

    public DefaultRandomStreamFactory(SeedDeriver seedDeriver) {
        this.seedDeriver = Objects.requireNonNull(seedDeriver, "seedDeriver");
    }

    @Override
    public DeterministicRandom create(RandomStreamKey key) {
        return new JavaDeterministicRandom(seedDeriver.derive(key));
    }
}

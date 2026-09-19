package com.wayfinder.core.random;

public interface RandomStreamFactory {
    DeterministicRandom create(RandomStreamKey key);
}

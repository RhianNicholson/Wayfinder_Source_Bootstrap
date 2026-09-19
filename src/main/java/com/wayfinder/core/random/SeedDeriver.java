package com.wayfinder.core.random;

public interface SeedDeriver {
    long derive(RandomStreamKey key);
}

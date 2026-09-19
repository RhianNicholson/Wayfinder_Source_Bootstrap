package com.wayfinder.core.random;

import java.util.Random;

final class JavaDeterministicRandom implements DeterministicRandom {
    private final Random random;

    JavaDeterministicRandom(long seed) {
        this.random = new Random(seed);
    }

    @Override public int nextInt(int bound) { return random.nextInt(bound); }
    @Override public double nextDouble() { return random.nextDouble(); }
    @Override public boolean nextBoolean() { return random.nextBoolean(); }
}

package com.wayfinder.core.random;

public interface DeterministicRandom {
    int nextInt(int bound);
    double nextDouble();
    boolean nextBoolean();
}

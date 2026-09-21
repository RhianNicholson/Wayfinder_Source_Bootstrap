package com.wayfinder.discovery;

import java.util.Set;

/**
 * Derived developer-facing discovery diagnostic.
 */
public record DiscoverySequenceResult(
        Set<DiscoveryBeat> beats,
        boolean coherent
) {
    public DiscoverySequenceResult {
        beats = Set.copyOf(beats);
    }
}

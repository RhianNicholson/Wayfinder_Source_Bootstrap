package com.wayfinder.history;

/**
 * Stable historical era identity.
 *
 * Era is a civilization-history concept, not Minecraft world time.
 */
public record HistoricalEraKey(int index) {
    public HistoricalEraKey {
        if (index < 0) {
            throw new IllegalArgumentException("Era index must be non-negative");
        }
    }

    public String stableKey() {
        return "wayfinder-era:" + index;
    }
}

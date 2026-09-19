package com.wayfinder.history;

import com.wayfinder.core.math.WorldPosition;

/**
 * Stable coarse historical scope derived from world geography.
 *
 * This is intentionally not a Minecraft chunk identity. It is a deterministic
 * bootstrap for Wayfinder semantic regions until the richer region lifecycle
 * owns explicit region identity.
 */
public record HistoricalRegionKey(int regionX, int regionZ) {
    public static final int REGION_SIZE = 512;

    public static HistoricalRegionKey from(WorldPosition position) {
        return new HistoricalRegionKey(
                Math.floorDiv(position.x(), REGION_SIZE),
                Math.floorDiv(position.z(), REGION_SIZE));
    }

    public String stableKey() {
        return "wayfinder-region:" + regionX + ":" + regionZ;
    }
}

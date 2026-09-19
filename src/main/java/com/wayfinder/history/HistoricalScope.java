package com.wayfinder.history;

import com.wayfinder.core.math.WorldPosition;

public record HistoricalScope(
        HistoricalRegionKey region,
        HistoricalEraKey era,
        int generationVersion
) {
    public HistoricalScope {
        if (generationVersion < 1) {
            throw new IllegalArgumentException("Generation version must be >= 1");
        }
    }

    public static HistoricalScope at(WorldPosition position, int era, int version) {
        return new HistoricalScope(
                HistoricalRegionKey.from(position),
                new HistoricalEraKey(era),
                version);
    }

    public String regionKey() {
        return region.stableKey();
    }

    public String eraKey() {
        return era.stableKey();
    }
}

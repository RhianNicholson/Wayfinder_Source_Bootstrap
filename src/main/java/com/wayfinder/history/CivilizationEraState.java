package com.wayfinder.history;

import java.util.Map;
import java.util.TreeMap;

/**
 * Current civilization era per semantic historical region.
 *
 * This is civilization history state, not Minecraft clock state.
 */
public record CivilizationEraState(Map<HistoricalRegionKey, CivilizationEra> erasByRegion) {
    public CivilizationEraState {
        erasByRegion = Map.copyOf(erasByRegion);
    }

    public static CivilizationEraState empty() {
        return new CivilizationEraState(Map.of());
    }

    public CivilizationEra eraFor(HistoricalRegionKey region) {
        return erasByRegion.getOrDefault(region, new CivilizationEra(1));
    }

    public CivilizationEraState advance(HistoricalRegionKey region) {
        var next = new java.util.HashMap<>(erasByRegion);
        next.put(region, eraFor(region).next());
        return new CivilizationEraState(next);
    }
}

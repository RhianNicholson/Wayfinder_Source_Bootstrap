package com.wayfinder.history;

public record CivilizationEraTransition(
        HistoricalRegionKey region,
        CivilizationEra from,
        CivilizationEra to
) {
    public CivilizationEraTransition {
        if (to.value() != from.value() + 1)
            throw new IllegalArgumentException(
                    "Era transitions must advance exactly one generation");
    }
}

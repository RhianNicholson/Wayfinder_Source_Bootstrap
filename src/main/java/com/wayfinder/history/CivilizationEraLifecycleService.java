package com.wayfinder.history;

/**
 * Explicit evaluate -> transition -> commit boundary for civilization eras.
 *
 * Nothing here reads Minecraft game time. A caller must deliberately request
 * historical advancement.
 */
public final class CivilizationEraLifecycleService {

    public CivilizationEraTransition evaluateAdvance(
            CivilizationEraState state,
            HistoricalRegionKey region
    ) {
        var current = state.eraFor(region);
        return new CivilizationEraTransition(region, current, current.next());
    }

    public CivilizationEraState commit(
            CivilizationEraState state,
            CivilizationEraTransition transition
    ) {
        var current = state.eraFor(transition.region());
        if (!current.equals(transition.from()))
            throw new IllegalStateException(
                    "Era state changed before transition commit");

        return state.advance(transition.region());
    }
}

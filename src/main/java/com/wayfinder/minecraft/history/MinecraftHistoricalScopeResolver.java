package com.wayfinder.minecraft.history;

import com.wayfinder.core.math.WorldPosition;
import com.wayfinder.history.HistoricalEraKey;
import com.wayfinder.history.HistoricalRegionKey;
import com.wayfinder.history.HistoricalScope;
import com.wayfinder.minecraft.persistence.MinecraftSavedDataCivilizationEraRepository;
import net.minecraft.server.level.ServerLevel;

/**
 * Resolves live historical scope from semantic geography plus persisted
 * civilization-owned regional era state.
 */
public final class MinecraftHistoricalScopeResolver {
    public static final int GENERATION_VERSION = 1;

    public HistoricalScope resolve(ServerLevel level, WorldPosition position) {
        var region = HistoricalRegionKey.from(position);
        var eras = new MinecraftSavedDataCivilizationEraRepository(
                level.getServer()).load();
        var era = eras.eraFor(region);

        return new HistoricalScope(
                region,
                new HistoricalEraKey(era.value()),
                GENERATION_VERSION);
    }
}

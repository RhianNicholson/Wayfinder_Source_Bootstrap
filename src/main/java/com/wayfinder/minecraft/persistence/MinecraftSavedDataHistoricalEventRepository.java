package com.wayfinder.minecraft.persistence;

import com.wayfinder.history.HistoricalEventState;
import net.minecraft.server.MinecraftServer;

public final class MinecraftSavedDataHistoricalEventRepository {
    private final MinecraftServer server;

    public MinecraftSavedDataHistoricalEventRepository(
            MinecraftServer server
    ) {
        this.server = server;
    }

    public HistoricalEventState load() {
        return data().toDomainState();
    }

    public void save(
            HistoricalEventState state
    ) {
        data().replace(state);
    }

    private WayfinderHistoricalEventSavedData data() {
        return server.getDataStorage()
                .computeIfAbsent(
                        WayfinderHistoricalEventSavedData.TYPE
                );
    }
}

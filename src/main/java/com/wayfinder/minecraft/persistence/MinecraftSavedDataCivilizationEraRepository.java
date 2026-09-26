package com.wayfinder.minecraft.persistence;

import com.wayfinder.history.CivilizationEraState;
import net.minecraft.server.MinecraftServer;

public final class MinecraftSavedDataCivilizationEraRepository {
    private final MinecraftServer server;

    public MinecraftSavedDataCivilizationEraRepository(MinecraftServer server) {
        this.server = server;
    }

    public CivilizationEraState load() {
        return data().toDomainState();
    }

    public void save(CivilizationEraState state) {
        data().replace(state);
    }

    private WayfinderCivilizationEraSavedData data() {
        return server.getDataStorage().computeIfAbsent(
                WayfinderCivilizationEraSavedData.TYPE);
    }
}

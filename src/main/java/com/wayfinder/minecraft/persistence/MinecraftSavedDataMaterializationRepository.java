package com.wayfinder.minecraft.persistence;

import com.wayfinder.structure.materialization.MaterializationState;
import net.minecraft.server.MinecraftServer;

public final class MinecraftSavedDataMaterializationRepository {
    private final MinecraftServer server;

    public MinecraftSavedDataMaterializationRepository(MinecraftServer server) {
        this.server = server;
    }

    public MaterializationState load() {
        return data().toDomainState();
    }

    public void save(MaterializationState state) {
        data().replace(state);
    }

    private WayfinderMaterializationSavedData data() {
        return server.getDataStorage().computeIfAbsent(
                WayfinderMaterializationSavedData.TYPE
        );
    }
}

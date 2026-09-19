package com.wayfinder.minecraft.persistence;

import com.wayfinder.civilization.persistence.CivilizationStateRepository;
import com.wayfinder.civilization.state.CivilizationState;
import net.minecraft.server.MinecraftServer;

public final class MinecraftSavedDataCivilizationRepository
        implements CivilizationStateRepository {

    private final WayfinderCivilizationSavedData savedData;

    public MinecraftSavedDataCivilizationRepository(MinecraftServer server) {
        this.savedData = server.getDataStorage()
                .computeIfAbsent(WayfinderCivilizationSavedData.TYPE);
    }

    @Override
    public CivilizationState load() {
        return savedData.toDomainState();
    }

    @Override
    public void save(CivilizationState state) {
        savedData.replace(state);
    }
}

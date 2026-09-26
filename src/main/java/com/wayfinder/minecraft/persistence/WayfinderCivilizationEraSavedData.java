package com.wayfinder.minecraft.persistence;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wayfinder.WayfinderMod;
import com.wayfinder.history.CivilizationEra;
import com.wayfinder.history.CivilizationEraState;
import com.wayfinder.history.HistoricalRegionKey;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;

import java.util.HashMap;
import java.util.List;

public final class WayfinderCivilizationEraSavedData extends SavedData {
    public static final int CURRENT_VERSION = 1;

    public record PersistedEra(int regionX, int regionZ, int era) {}

    private static final Codec<PersistedEra> ERA_CODEC =
            RecordCodecBuilder.create(instance -> instance.group(
                    Codec.INT.fieldOf("region_x").forGetter(PersistedEra::regionX),
                    Codec.INT.fieldOf("region_z").forGetter(PersistedEra::regionZ),
                    Codec.INT.fieldOf("era").forGetter(PersistedEra::era)
            ).apply(instance, PersistedEra::new));

    private static final Codec<WayfinderCivilizationEraSavedData> CODEC =
            RecordCodecBuilder.create(instance -> instance.group(
                    Codec.INT.optionalFieldOf("version", CURRENT_VERSION)
                            .forGetter(WayfinderCivilizationEraSavedData::version),
                    ERA_CODEC.listOf().optionalFieldOf("regions", List.of())
                            .forGetter(WayfinderCivilizationEraSavedData::regions)
            ).apply(instance, WayfinderCivilizationEraSavedData::new));

    public static final SavedDataType<WayfinderCivilizationEraSavedData> TYPE =
            new SavedDataType<>(
                    Identifier.fromNamespaceAndPath(
                            WayfinderMod.MOD_ID, "civilization_eras"),
                    WayfinderCivilizationEraSavedData::new,
                    CODEC
            );

    private final int version;
    private List<PersistedEra> regions;

    public WayfinderCivilizationEraSavedData() {
        this(CURRENT_VERSION, List.of());
    }

    private WayfinderCivilizationEraSavedData(
            int version,
            List<PersistedEra> regions
    ) {
        if (version > CURRENT_VERSION)
            throw new IllegalStateException(
                    "Wayfinder civilization era data is newer than supported");
        this.version = version;
        this.regions = List.copyOf(regions);
    }

    public int version() { return version; }
    public List<PersistedEra> regions() { return regions; }

    public CivilizationEraState toDomainState() {
        var eras = new HashMap<HistoricalRegionKey, CivilizationEra>();
        for (var persisted : regions) {
            var region = new HistoricalRegionKey(
                    persisted.regionX(), persisted.regionZ());
            var previous = eras.put(
                    region, new CivilizationEra(persisted.era()));
            if (previous != null)
                throw new IllegalStateException(
                        "Duplicate persisted civilization era region: "
                                + region.stableKey());
        }
        return new CivilizationEraState(eras);
    }

    public void replace(CivilizationEraState state) {
        var next = state.erasByRegion().entrySet().stream()
                .sorted(java.util.Comparator
                        .comparingInt((java.util.Map.Entry<HistoricalRegionKey, CivilizationEra> e)
                                -> e.getKey().regionX())
                        .thenComparingInt(e -> e.getKey().regionZ()))
                .map(e -> new PersistedEra(
                        e.getKey().regionX(),
                        e.getKey().regionZ(),
                        e.getValue().value()))
                .toList();

        if (regions.equals(next)) return;
        regions = List.copyOf(next);
        setDirty();
    }
}

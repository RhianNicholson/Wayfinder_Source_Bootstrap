package com.wayfinder.minecraft.persistence;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wayfinder.WayfinderMod;
import com.wayfinder.structure.materialization.MaterializationState;
import com.wayfinder.structure.materialization.persistence.MaterializationPersistenceMapper;
import com.wayfinder.structure.materialization.persistence.PersistedMaterializationRecord;
import com.wayfinder.structure.materialization.persistence.PersistedMaterializedCell;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;

import java.util.List;

public final class WayfinderMaterializationSavedData extends SavedData {
    public static final int CURRENT_VERSION = 1;

    private static final Codec<PersistedMaterializedCell> CELL_CODEC =
            RecordCodecBuilder.create(instance -> instance.group(
                    Codec.INT.fieldOf("x").forGetter(PersistedMaterializedCell::x),
                    Codec.INT.fieldOf("y").forGetter(PersistedMaterializedCell::y),
                    Codec.INT.fieldOf("z").forGetter(PersistedMaterializedCell::z),
                    Codec.STRING.fieldOf("material_role").forGetter(PersistedMaterializedCell::materialRole),
                    Codec.STRING.fieldOf("function").forGetter(PersistedMaterializedCell::function)
            ).apply(instance, PersistedMaterializedCell::new));

    private static final Codec<PersistedMaterializationRecord> RECORD_CODEC =
            RecordCodecBuilder.create(instance -> instance.group(
                    Codec.STRING.fieldOf("source_node_id").forGetter(PersistedMaterializationRecord::sourceNodeId),
                    Codec.STRING.fieldOf("archetype").forGetter(PersistedMaterializationRecord::archetype),
                    Codec.INT.fieldOf("format_version").forGetter(PersistedMaterializationRecord::formatVersion),
                    Codec.INT.fieldOf("palette_version").forGetter(PersistedMaterializationRecord::paletteVersion),
                    Codec.STRING.fieldOf("condition").forGetter(PersistedMaterializationRecord::condition),
                    CELL_CODEC.listOf().fieldOf("original_cells").forGetter(PersistedMaterializationRecord::originalCells)
            ).apply(instance, PersistedMaterializationRecord::new));

    private static final Codec<WayfinderMaterializationSavedData> CODEC =
            RecordCodecBuilder.create(instance -> instance.group(
                    Codec.INT.optionalFieldOf("version", CURRENT_VERSION)
                            .forGetter(WayfinderMaterializationSavedData::version),
                    RECORD_CODEC.listOf().optionalFieldOf("records", List.of())
                            .forGetter(WayfinderMaterializationSavedData::records)
            ).apply(instance, WayfinderMaterializationSavedData::new));

    public static final SavedDataType<WayfinderMaterializationSavedData> TYPE =
            new SavedDataType<>(
                    Identifier.fromNamespaceAndPath(WayfinderMod.MOD_ID, "materialization"),
                    WayfinderMaterializationSavedData::new,
                    CODEC
            );

    private final int version;
    private List<PersistedMaterializationRecord> records;

    public WayfinderMaterializationSavedData() {
        this(CURRENT_VERSION, List.of());
    }

    private WayfinderMaterializationSavedData(
            int version,
            List<PersistedMaterializationRecord> records
    ) {
        if (version > CURRENT_VERSION) {
            throw new IllegalStateException("Wayfinder materialization data is newer than supported");
        }
        this.version = version;
        this.records = List.copyOf(records);
    }

    public int version() { return version; }
    public List<PersistedMaterializationRecord> records() { return records; }

    public MaterializationState toDomainState() {
        return MaterializationPersistenceMapper.toDomain(records);
    }

    public void replace(MaterializationState state) {
        var next = MaterializationPersistenceMapper.toPersisted(state);
        if (records.equals(next)) return;
        records = List.copyOf(next);
        setDirty();
    }
}

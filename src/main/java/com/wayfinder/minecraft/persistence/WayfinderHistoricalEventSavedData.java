package com.wayfinder.minecraft.persistence;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wayfinder.WayfinderMod;
import com.wayfinder.history.HistoricalEventState;
import com.wayfinder.history.persistence.HistoricalEventPersistenceMapper;
import com.wayfinder.history.persistence.PersistedHistoricalEvent;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;

import java.util.List;

public final class WayfinderHistoricalEventSavedData
        extends SavedData {

    public static final int CURRENT_VERSION = 1;

    private static final Codec<PersistedHistoricalEvent>
            EVENT_CODEC =
            RecordCodecBuilder.create(instance -> instance.group(
                    Codec.STRING.fieldOf("id")
                            .forGetter(PersistedHistoricalEvent::id),
                    Codec.LONG.fieldOf("sequence")
                            .forGetter(PersistedHistoricalEvent::sequence),
                    Codec.STRING.fieldOf("type")
                            .forGetter(PersistedHistoricalEvent::type),
                    Codec.STRING.fieldOf("affected_node_id")
                            .forGetter(
                                    PersistedHistoricalEvent
                                            ::affectedNodeId
                            ),
                    Codec.STRING.fieldOf("cause")
                            .forGetter(PersistedHistoricalEvent::cause)
            ).apply(instance, PersistedHistoricalEvent::new));

    private static final Codec<WayfinderHistoricalEventSavedData>
            CODEC =
            RecordCodecBuilder.create(instance -> instance.group(
                    Codec.INT.optionalFieldOf(
                                    "version",
                                    CURRENT_VERSION
                            )
                            .forGetter(
                                    WayfinderHistoricalEventSavedData::version
                            ),
                    EVENT_CODEC.listOf()
                            .optionalFieldOf(
                                    "events",
                                    List.of()
                            )
                            .forGetter(
                                    WayfinderHistoricalEventSavedData::events
                            )
            ).apply(
                    instance,
                    WayfinderHistoricalEventSavedData::new
            ));

    public static final SavedDataType<
            WayfinderHistoricalEventSavedData
            > TYPE =
            new SavedDataType<>(
                    Identifier.fromNamespaceAndPath(
                            WayfinderMod.MOD_ID,
                            "history_events"
                    ),
                    WayfinderHistoricalEventSavedData::new,
                    CODEC
            );

    private final int version;
    private List<PersistedHistoricalEvent> events;

    public WayfinderHistoricalEventSavedData() {
        this(
                CURRENT_VERSION,
                List.of()
        );
    }

    private WayfinderHistoricalEventSavedData(
            int version,
            List<PersistedHistoricalEvent> events
    ) {
        if (version > CURRENT_VERSION) {
            throw new IllegalStateException(
                    "Wayfinder historical event data is newer than supported"
            );
        }

        this.version = version;
        this.events = List.copyOf(events);
    }

    public int version() {
        return version;
    }

    public List<PersistedHistoricalEvent> events() {
        return events;
    }

    public HistoricalEventState toDomainState() {
        return HistoricalEventPersistenceMapper.toDomain(
                events
        );
    }

    public void replace(
            HistoricalEventState state
    ) {
        var next =
                HistoricalEventPersistenceMapper.toPersisted(
                        state
                );

        if (events.equals(next)) {
            return;
        }

        events = List.copyOf(next);
        setDirty();
    }
}

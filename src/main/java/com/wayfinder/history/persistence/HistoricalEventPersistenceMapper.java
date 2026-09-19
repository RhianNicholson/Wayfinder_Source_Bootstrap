package com.wayfinder.history.persistence;

import com.wayfinder.core.id.NodeId;
import com.wayfinder.history.HistoricalEvent;
import com.wayfinder.history.HistoricalEventState;
import com.wayfinder.history.HistoricalEventType;

import java.util.List;
import java.util.UUID;

public final class HistoricalEventPersistenceMapper {
    private HistoricalEventPersistenceMapper() {}

    public static List<PersistedHistoricalEvent> toPersisted(
            HistoricalEventState state
    ) {
        return state.events()
                .stream()
                .map(event ->
                        new PersistedHistoricalEvent(
                                event.id().toString(),
                                event.sequence(),
                                event.type().name(),
                                event.affectedNodeId()
                                        .value()
                                        .toString(),
                                event.cause()
                        )
                )
                .toList();
    }

    public static HistoricalEventState toDomain(
            List<PersistedHistoricalEvent> events
    ) {
        return new HistoricalEventState(
                events.stream()
                        .map(event ->
                                new HistoricalEvent(
                                        UUID.fromString(
                                                event.id()
                                        ),
                                        event.sequence(),
                                        HistoricalEventType.valueOf(
                                                event.type()
                                        ),
                                        new NodeId(
                                                UUID.fromString(
                                                        event.affectedNodeId()
                                                )
                                        ),
                                        event.cause()
                                )
                        )
                        .toList()
        );
    }
}

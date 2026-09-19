package com.wayfinder.history;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public record HistoricalEventState(
        List<HistoricalEvent> events
) {
    public HistoricalEventState {
        events = List.copyOf(events);

        long uniqueIds =
                events.stream()
                        .map(HistoricalEvent::id)
                        .distinct()
                        .count();

        if (uniqueIds != events.size()) {
            throw new IllegalArgumentException(
                    "Historical event IDs must be unique"
            );
        }

        for (int i = 0; i < events.size(); i++) {
            long expected = i + 1L;
            if (events.get(i).sequence() != expected) {
                throw new IllegalArgumentException(
                        "Historical event sequence must be contiguous and ordered"
                );
            }
        }
    }

    public static HistoricalEventState empty() {
        return new HistoricalEventState(List.of());
    }

    public long nextSequence() {
        return events.size() + 1L;
    }

    public Optional<HistoricalEvent> find(UUID id) {
        return events.stream()
                .filter(event -> event.id().equals(id))
                .findFirst();
    }
}

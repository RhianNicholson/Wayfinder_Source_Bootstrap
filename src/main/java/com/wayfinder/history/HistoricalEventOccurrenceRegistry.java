package com.wayfinder.history;

import java.util.EnumMap;
import java.util.List;
import java.util.Optional;

public final class HistoricalEventOccurrenceRegistry {
    private final EnumMap<HistoricalEventType, HistoricalEventOccurrenceDecider>
            deciders = new EnumMap<>(HistoricalEventType.class);

    public HistoricalEventOccurrenceRegistry(
            List<HistoricalEventOccurrenceDecider> deciders
    ) {
        for (var decider : deciders) {
            var previous = this.deciders.put(decider.type(), decider);
            if (previous != null)
                throw new IllegalArgumentException(
                        "Duplicate historical occurrence decider: " + decider.type());
        }
    }

    public Optional<HistoricalEventOccurrenceDecider> deciderFor(
            HistoricalEventType type
    ) {
        return Optional.ofNullable(deciders.get(type));
    }
}

package com.wayfinder.history;
import java.util.Optional;
public record HistoricalEventHandlerResult(HistoricalEventState state, Optional<HistoricalEvent> committedEvent) {
 public static HistoricalEventHandlerResult unchanged(HistoricalEventState state) { return new HistoricalEventHandlerResult(state, Optional.empty()); }
}

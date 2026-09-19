package com.wayfinder.history;

import com.wayfinder.core.id.NodeId;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

public final class HistoricalEventFactory {

    public HistoricalEvent routeLoss(
            HistoricalEventState state,
            NodeId affectedNodeId,
            String cause
    ) {
        String key =
                "wayfinder-history:route-loss:"
                        + affectedNodeId.value();

        UUID id =
                UUID.nameUUIDFromBytes(
                        key.getBytes(StandardCharsets.UTF_8)
                );

        return new HistoricalEvent(
                id,
                state.nextSequence(),
                HistoricalEventType.ROUTE_LOSS,
                affectedNodeId,
                cause
        );
    }
}

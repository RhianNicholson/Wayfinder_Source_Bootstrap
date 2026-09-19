package com.wayfinder.history;

import com.wayfinder.core.id.NodeId;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

final class HistoricalEventCommitServiceTest {

    private final NodeId nodeId =
            new NodeId(
                    UUID.fromString(
                            "11111111-1111-1111-1111-111111111111"
                    )
            );

    @Test
    void routeLossBecomesAppendOnlyHistoricalTruth() {
        var state = HistoricalEventState.empty();

        var event =
                new HistoricalEventFactory()
                        .routeLoss(
                                state,
                                nodeId,
                                "route collapse"
                        );

        var result =
                new HistoricalEventCommitService()
                        .commit(
                                state,
                                event
                        );

        assertTrue(result.committed());
        assertEquals(1, result.state().events().size());
        assertEquals(
                HistoricalEventType.ROUTE_LOSS,
                result.event().type()
        );
    }

    @Test
    void sameRouteLossCannotBeCommittedTwice() {
        var factory = new HistoricalEventFactory();
        var service = new HistoricalEventCommitService();

        var firstEvent =
                factory.routeLoss(
                        HistoricalEventState.empty(),
                        nodeId,
                        "route collapse"
                );

        var first =
                service.commit(
                        HistoricalEventState.empty(),
                        firstEvent
                );

        var duplicate =
                factory.routeLoss(
                        first.state(),
                        nodeId,
                        "different explanation cannot rewrite event"
                );

        var second =
                service.commit(
                        first.state(),
                        duplicate
                );

        assertFalse(second.committed());
        assertEquals(1, second.state().events().size());
        assertEquals(
                "route collapse",
                second.event().cause()
        );
    }

    @Test
    void eventSequenceCannotSkipHistory() {
        var state = HistoricalEventState.empty();

        var invalid =
                new HistoricalEvent(
                        UUID.randomUUID(),
                        2,
                        HistoricalEventType.ROUTE_LOSS,
                        nodeId,
                        "invalid sequence"
                );

        var result =
                new HistoricalEventCommitService()
                        .commit(
                                state,
                                invalid
                        );

        assertFalse(result.committed());
        assertTrue(result.state().events().isEmpty());
    }
}

package com.wayfinder.history;

import java.util.ArrayList;

public final class HistoricalEventCommitService {

    public Result commit(
            HistoricalEventState current,
            HistoricalEvent event
    ) {
        var existing = current.find(event.id());

        if (existing.isPresent()) {
            return new Result(
                    false,
                    current,
                    existing.get()
            );
        }

        if (event.sequence() != current.nextSequence()) {
            return new Result(
                    false,
                    current,
                    event
            );
        }

        var next = new ArrayList<>(current.events());
        next.add(event);

        return new Result(
                true,
                new HistoricalEventState(next),
                event
        );
    }

    public record Result(
            boolean committed,
            HistoricalEventState state,
            HistoricalEvent event
    ) {}
}

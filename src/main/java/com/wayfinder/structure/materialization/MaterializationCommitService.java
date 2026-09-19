package com.wayfinder.structure.materialization;

import java.util.ArrayList;

public final class MaterializationCommitService {

    public Result recordPlacement(
            MaterializationState current,
            MaterializationRecord record
    ) {
        var existing = current.find(record.sourceNodeId());

        if (existing.isPresent()) {
            return new Result(false, current, existing.get());
        }

        var next = new ArrayList<>(current.records());
        next.add(record);

        return new Result(
                true,
                new MaterializationState(next),
                record
        );
    }

    public Result updateCondition(
            MaterializationState current,
            MaterializationRecord record,
            MaterializationCondition condition
    ) {
        var existing = current.find(record.sourceNodeId());
        if (existing.isEmpty()) {
            return new Result(false, current, record);
        }

        var old = existing.get();
        var updated = new MaterializationRecord(
                old.sourceNodeId(),
                old.archetype(),
                old.formatVersion(),
                old.paletteVersion(),
                condition,
                old.originalCells()
        );

        var next = current.records().stream()
                .map(r -> r.sourceNodeId().equals(updated.sourceNodeId()) ? updated : r)
                .toList();

        return new Result(true, new MaterializationState(next), updated);
    }

    public record Result(
            boolean committed,
            MaterializationState state,
            MaterializationRecord record
    ) {}
}

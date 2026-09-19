package com.wayfinder.structure.materialization;

import com.wayfinder.core.id.NodeId;

import java.util.List;
import java.util.Optional;

public record MaterializationState(
        List<MaterializationRecord> records
) {
    public MaterializationState {
        records = List.copyOf(records);
        long unique = records.stream().map(MaterializationRecord::sourceNodeId).distinct().count();
        if (unique != records.size()) {
            throw new IllegalArgumentException("Only one materialization record may exist per source node");
        }
    }

    public static MaterializationState empty() {
        return new MaterializationState(List.of());
    }

    public Optional<MaterializationRecord> find(NodeId sourceNodeId) {
        return records.stream()
                .filter(record -> record.sourceNodeId().equals(sourceNodeId))
                .findFirst();
    }
}

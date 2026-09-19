package com.wayfinder.structure.materialization.persistence;

public record PersistedMaterializedCell(
        int x,
        int y,
        int z,
        String materialRole,
        String function
) {}

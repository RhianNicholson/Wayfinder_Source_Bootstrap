package com.wayfinder.civilization.persistence;

import com.wayfinder.core.math.WorldPosition;

public record PersistedPosition(int x, int y, int z) {

    public static PersistedPosition fromDomain(WorldPosition position) {
        return new PersistedPosition(position.x(), position.y(), position.z());
    }

    public WorldPosition toDomain() {
        return new WorldPosition(x, y, z);
    }
}

package com.wayfinder.core.math;

public record WorldBounds(
    int minX, int minY, int minZ,
    int maxX, int maxY, int maxZ
) {
    public WorldBounds {
        if (minX > maxX || minY > maxY || minZ > maxZ) {
            throw new IllegalArgumentException("WorldBounds minimums must not exceed maximums");
        }
    }

    public int width() { return maxX - minX + 1; }
    public int height() { return maxY - minY + 1; }
    public int depth() { return maxZ - minZ + 1; }

    public boolean contains(WorldPosition position) {
        return position.x() >= minX && position.x() <= maxX
            && position.y() >= minY && position.y() <= maxY
            && position.z() >= minZ && position.z() <= maxZ;
    }
}

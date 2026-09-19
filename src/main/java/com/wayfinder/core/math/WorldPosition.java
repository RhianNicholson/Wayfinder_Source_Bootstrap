package com.wayfinder.core.math;

public record WorldPosition(int x, int y, int z) {
    public HorizontalPosition horizontal() {
        return new HorizontalPosition(x, z);
    }

    public double horizontalDistanceTo(WorldPosition other) {
        long dx = (long) other.x - x;
        long dz = (long) other.z - z;
        return Math.sqrt((double) dx * dx + (double) dz * dz);
    }
}

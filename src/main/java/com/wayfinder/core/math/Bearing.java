package com.wayfinder.core.math;

public record Bearing(double degrees) {
    public Bearing {
        degrees = normalize(degrees);
    }

    private static double normalize(double value) {
        double result = value % 360.0;
        return result < 0.0 ? result + 360.0 : result;
    }

    public static Bearing between(WorldPosition source, WorldPosition target) {
        double dx = target.x() - source.x();
        double dz = target.z() - source.z();
        return new Bearing(Math.toDegrees(Math.atan2(dz, dx)));
    }

    public double angularDistance(Bearing other) {
        double delta = Math.abs(degrees - other.degrees);
        return Math.min(delta, 360.0 - delta);
    }
}

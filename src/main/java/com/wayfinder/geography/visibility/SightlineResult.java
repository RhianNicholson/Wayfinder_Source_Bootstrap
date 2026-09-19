package com.wayfinder.geography.visibility;

public record SightlineResult(
        boolean visible,
        double score,
        int visibleSamples,
        int totalSamples,
        double obstruction
) {
    public SightlineResult {
        if (score < 0.0 || score > 1.0) throw new IllegalArgumentException("score must be 0..1");
        if (obstruction < 0.0 || obstruction > 1.0) throw new IllegalArgumentException("obstruction must be 0..1");
        if (visibleSamples < 0 || totalSamples < 1 || visibleSamples > totalSamples) {
            throw new IllegalArgumentException("invalid sample counts");
        }
    }
}

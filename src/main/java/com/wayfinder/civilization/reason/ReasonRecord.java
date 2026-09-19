package com.wayfinder.civilization.reason;

public record ReasonRecord(
        ReasonType type,
        double contribution,
        String developerExplanation
) {
    public ReasonRecord {
        if (contribution < 0.0 || contribution > 1.0) {
            throw new IllegalArgumentException("contribution must be 0..1");
        }
        if (developerExplanation == null || developerExplanation.isBlank()) {
            throw new IllegalArgumentException("developerExplanation is required");
        }
    }
}

package com.wayfinder.structure.site;

import java.util.List;

public record ShrineSiteValidationResult(
        boolean accepted,
        int minimumSurfaceY,
        int maximumSurfaceY,
        int maximumFoundationDrop,
        List<ShrineSiteIssue> issues
) {
    public ShrineSiteValidationResult {
        issues = List.copyOf(issues);

        if (accepted && !issues.isEmpty()) {
            throw new IllegalArgumentException(
                    "Accepted site validation cannot contain issues"
            );
        }

        if (!accepted && issues.isEmpty()) {
            throw new IllegalArgumentException(
                    "Rejected site validation requires at least one issue"
            );
        }
    }

    public int terrainSpread() {
        return maximumSurfaceY - minimumSurfaceY;
    }
}

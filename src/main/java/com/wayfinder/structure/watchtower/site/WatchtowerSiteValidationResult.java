package com.wayfinder.structure.watchtower.site;

import java.util.List;

public record WatchtowerSiteValidationResult(
        boolean accepted,
        int maximumSupportExtension,
        List<WatchtowerSiteIssue> issues
) {
    public WatchtowerSiteValidationResult {
        issues = List.copyOf(issues);
        if (accepted && !issues.isEmpty()) {
            throw new IllegalArgumentException("accepted validation cannot contain issues");
        }
        if (!accepted && issues.isEmpty()) {
            throw new IllegalArgumentException("rejected validation requires issues");
        }
    }
}

package com.wayfinder.structure.watchtower.site;

public record WatchtowerSiteIssue(
        WatchtowerSiteIssueCode code,
        String message
) {
    public WatchtowerSiteIssue {
        if (code == null) throw new IllegalArgumentException("code is required");
        if (message == null || message.isBlank()) {
            throw new IllegalArgumentException("message is required");
        }
    }
}

package com.wayfinder.structure.site;

public record ShrineSiteIssue(
        ShrineSiteIssueCode code,
        String message
) {
    public ShrineSiteIssue {
        if (code == null) {
            throw new IllegalArgumentException("code is required");
        }
        if (message == null || message.isBlank()) {
            throw new IllegalArgumentException("message is required");
        }
    }
}

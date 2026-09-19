package com.wayfinder.civilization.network;

import java.util.List;

public record NetworkValidationResult(
        boolean accepted,
        List<NetworkValidationIssue> issues
) {
    public NetworkValidationResult {
        issues = List.copyOf(issues);
        if (accepted && !issues.isEmpty()) {
            throw new IllegalArgumentException("accepted result cannot contain issues");
        }
    }

    public static NetworkValidationResult allow() {
        return new NetworkValidationResult(true, List.of());
    }

    public static NetworkValidationResult reject(List<NetworkValidationIssue> issues) {
        if (issues.isEmpty()) {
            throw new IllegalArgumentException("rejected result requires issues");
        }
        return new NetworkValidationResult(false, issues);
    }
}

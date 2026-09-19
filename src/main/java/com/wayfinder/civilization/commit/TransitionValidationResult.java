package com.wayfinder.civilization.commit;

import java.util.List;

public record TransitionValidationResult(
        boolean accepted,
        List<TransitionValidationIssue> issues
) {
    public TransitionValidationResult {
        issues = List.copyOf(issues);
        if (accepted && !issues.isEmpty()) {
            throw new IllegalArgumentException("accepted transition cannot contain issues");
        }
    }

    public static TransitionValidationResult allow() {
        return new TransitionValidationResult(true, List.of());
    }

    public static TransitionValidationResult reject(List<TransitionValidationIssue> issues) {
        if (issues.isEmpty()) {
            throw new IllegalArgumentException("rejected transition requires issues");
        }
        return new TransitionValidationResult(false, issues);
    }
}

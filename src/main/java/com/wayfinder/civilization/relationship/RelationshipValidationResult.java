package com.wayfinder.civilization.relationship;

import java.util.List;

public record RelationshipValidationResult(
        boolean accepted,
        List<RelationshipValidationIssue> issues
) {
    public RelationshipValidationResult {
        issues = List.copyOf(issues);

        if (accepted && !issues.isEmpty()) {
            throw new IllegalArgumentException(
                    "accepted relationship cannot contain validation issues"
            );
        }
    }

    public static RelationshipValidationResult allow() {
        return new RelationshipValidationResult(true, List.of());
    }

    public static RelationshipValidationResult reject(
            List<RelationshipValidationIssue> issues
    ) {
        if (issues.isEmpty()) {
            throw new IllegalArgumentException(
                    "rejected relationship requires at least one issue"
            );
        }

        return new RelationshipValidationResult(false, issues);
    }
}

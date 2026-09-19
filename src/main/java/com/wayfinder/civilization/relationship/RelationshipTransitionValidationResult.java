package com.wayfinder.civilization.relationship;

import java.util.List;

public record RelationshipTransitionValidationResult(
        boolean accepted,
        List<RelationshipTransitionValidationIssue> issues
) {
    public RelationshipTransitionValidationResult {
        issues = List.copyOf(issues);

        if (accepted && !issues.isEmpty()) {
            throw new IllegalArgumentException(
                    "accepted transition cannot contain issues"
            );
        }
    }

    public static RelationshipTransitionValidationResult allow() {
        return new RelationshipTransitionValidationResult(
                true,
                List.of()
        );
    }

    public static RelationshipTransitionValidationResult reject(
            List<RelationshipTransitionValidationIssue> issues
    ) {
        if (issues.isEmpty()) {
            throw new IllegalArgumentException(
                    "rejected transition requires issues"
            );
        }

        return new RelationshipTransitionValidationResult(
                false,
                issues
        );
    }
}

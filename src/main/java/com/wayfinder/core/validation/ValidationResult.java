package com.wayfinder.core.validation;

import java.util.List;

public record ValidationResult(List<ValidationIssue> issues) {
    public ValidationResult {
        issues = List.copyOf(issues);
    }

    public boolean valid() {
        return issues.stream().noneMatch(issue -> issue.severity() == ValidationSeverity.ERROR);
    }

    public static ValidationResult success() {
        return new ValidationResult(List.of());
    }
}

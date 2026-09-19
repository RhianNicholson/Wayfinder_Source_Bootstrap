package com.wayfinder.civilization.commit;

public record TransitionValidationIssue(
        TransitionValidationCode code,
        String explanation
) {}

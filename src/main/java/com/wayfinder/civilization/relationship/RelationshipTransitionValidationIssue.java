package com.wayfinder.civilization.relationship;

public record RelationshipTransitionValidationIssue(
        RelationshipTransitionValidationCode code,
        String explanation
) {}

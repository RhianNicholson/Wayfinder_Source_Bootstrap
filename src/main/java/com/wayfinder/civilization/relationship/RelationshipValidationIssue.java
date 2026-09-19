package com.wayfinder.civilization.relationship;

public record RelationshipValidationIssue(
        RelationshipValidationCode code,
        String explanation
) {}

package com.wayfinder.civilization.relationship;

public record RelationshipAdmissionDecision(
        RelationshipProposal proposal,
        RelationshipValidationResult validation
) {
    public boolean accepted() {
        return validation.accepted();
    }
}

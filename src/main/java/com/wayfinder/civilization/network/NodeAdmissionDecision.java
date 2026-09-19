package com.wayfinder.civilization.network;

public record NodeAdmissionDecision(
        NodeProposal proposal,
        NetworkValidationResult validation
) {
    public boolean accepted() {
        return validation.accepted();
    }
}

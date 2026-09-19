package com.wayfinder.civilization.relationship;

public final class RelationshipAdmissionService {
    private final RelationshipProposalValidator validator;

    public RelationshipAdmissionService(
            RelationshipProposalValidator validator
    ) {
        this.validator = validator;
    }

    public RelationshipAdmissionDecision evaluate(
            RelationshipProposal proposal,
            RelationshipValidationContext context
    ) {
        return new RelationshipAdmissionDecision(
                proposal,
                validator.validate(proposal, context)
        );
    }
}

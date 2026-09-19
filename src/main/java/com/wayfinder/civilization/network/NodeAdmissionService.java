package com.wayfinder.civilization.network;

import com.wayfinder.civilization.direction.DirectionDecision;
import com.wayfinder.civilization.service.ObservationDecision;

public final class NodeAdmissionService {
    private final NodeProposalFactory proposalFactory;
    private final NodeProposalValidator validator;

    public NodeAdmissionService(
            NodeProposalFactory proposalFactory,
            NodeProposalValidator validator
    ) {
        this.proposalFactory = proposalFactory;
        this.validator = validator;
    }

    public NodeAdmissionDecision evaluate(
            ObservationDecision decision,
            NetworkValidationContext context
    ) {
        return evaluate(
                proposalFactory.fromObservationDecision(
                        decision
                ),
                context
        );
    }

    public NodeAdmissionDecision evaluate(
            DirectionDecision decision,
            NetworkValidationContext context
    ) {
        return evaluate(
                proposalFactory.fromDirectionDecision(
                        decision
                ),
                context
        );
    }

    public NodeAdmissionDecision evaluate(
            NodeProposal proposal,
            NetworkValidationContext context
    ) {
        NetworkValidationResult validation =
                validator.validate(
                        proposal,
                        context
                );

        return new NodeAdmissionDecision(
                proposal,
                validation
        );
    }
}

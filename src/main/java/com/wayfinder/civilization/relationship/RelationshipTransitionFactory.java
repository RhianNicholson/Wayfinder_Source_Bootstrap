package com.wayfinder.civilization.relationship;

import java.util.Optional;

public final class RelationshipTransitionFactory {
    private final RelationshipIdentityFactory identityFactory;

    public RelationshipTransitionFactory(
            RelationshipIdentityFactory identityFactory
    ) {
        this.identityFactory = identityFactory;
    }

    public Optional<RelationshipTransition> create(
            RelationshipAdmissionDecision admission
    ) {
        if (!admission.accepted()) {
            return Optional.empty();
        }

        RelationshipProposal proposal = admission.proposal();

        CivilizationRelationship relationship =
                new CivilizationRelationship(
                        identityFactory.fromProposal(proposal),
                        proposal.type(),
                        proposal.sourceNodeId(),
                        proposal.targetNodeId(),
                        proposal.score(),
                        proposal.reasons(),
                        proposal.proposalKey()
                );

        return Optional.of(
                new RelationshipTransition(
                        proposal,
                        relationship
                )
        );
    }
}

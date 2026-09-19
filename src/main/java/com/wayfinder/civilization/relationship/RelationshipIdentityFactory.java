package com.wayfinder.civilization.relationship;

import com.wayfinder.core.id.RelationshipId;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

public final class RelationshipIdentityFactory {

    public RelationshipId fromProposal(RelationshipProposal proposal) {
        String identity = String.join(
                ":",
                "wayfinder-relationship",
                proposal.type().name(),
                proposal.sourceNodeId().value().toString(),
                proposal.targetNodeId().value().toString(),
                proposal.proposalKey()
        );

        return new RelationshipId(
                UUID.nameUUIDFromBytes(
                        identity.getBytes(StandardCharsets.UTF_8)
                )
        );
    }
}

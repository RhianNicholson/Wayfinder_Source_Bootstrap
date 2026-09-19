package com.wayfinder.civilization.direction;

import com.wayfinder.civilization.commit.NodeCommitService;
import com.wayfinder.civilization.commit.NodeTransitionFactory;
import com.wayfinder.civilization.network.NetworkValidationContext;
import com.wayfinder.civilization.network.NodeAdmissionService;
import com.wayfinder.civilization.relationship.DirectionalReferenceProposalFactory;
import com.wayfinder.civilization.relationship.RelationshipAdmissionService;
import com.wayfinder.civilization.relationship.RelationshipCommitService;
import com.wayfinder.civilization.relationship.RelationshipTransitionFactory;
import com.wayfinder.civilization.relationship.RelationshipValidationContext;
import com.wayfinder.civilization.state.CivilizationState;

public final class DirectionChainCommitService {
    private final NodeAdmissionService nodeAdmissionService;
    private final NodeTransitionFactory nodeTransitionFactory;
    private final NodeCommitService nodeCommitService;
    private final DirectionalReferenceProposalFactory
            relationshipProposalFactory;
    private final RelationshipAdmissionService
            relationshipAdmissionService;
    private final RelationshipTransitionFactory
            relationshipTransitionFactory;
    private final RelationshipCommitService
            relationshipCommitService;

    public DirectionChainCommitService(
            NodeAdmissionService nodeAdmissionService,
            NodeTransitionFactory nodeTransitionFactory,
            NodeCommitService nodeCommitService,
            DirectionalReferenceProposalFactory relationshipProposalFactory,
            RelationshipAdmissionService relationshipAdmissionService,
            RelationshipTransitionFactory relationshipTransitionFactory,
            RelationshipCommitService relationshipCommitService
    ) {
        this.nodeAdmissionService =
                nodeAdmissionService;
        this.nodeTransitionFactory =
                nodeTransitionFactory;
        this.nodeCommitService =
                nodeCommitService;
        this.relationshipProposalFactory =
                relationshipProposalFactory;
        this.relationshipAdmissionService =
                relationshipAdmissionService;
        this.relationshipTransitionFactory =
                relationshipTransitionFactory;
        this.relationshipCommitService =
                relationshipCommitService;
    }

    /**
     * Produces a new state only if both the Direction node and its required
     * directional relationship can be committed. The caller persists only
     * the returned state when committed=true.
     */
    public DirectionChainCommitResult commit(
            DirectionDecision decision,
            CivilizationState current
    ) {
        NetworkValidationContext networkContext =
                new NetworkValidationContext(
                        current.nodes()
                                .stream()
                                .map(node ->
                                        new com.wayfinder.civilization.network.CommittedNodeSnapshot(
                                                node.id(),
                                                node.purpose(),
                                                node.position()
                                        )
                                )
                                .toList()
                );

        var nodeAdmission =
                nodeAdmissionService.evaluate(
                        decision,
                        networkContext
                );

        if (!nodeAdmission.accepted()) {
            return new DirectionChainCommitResult(
                    false,
                    current,
                    "NODE_ADMISSION_REJECTED"
            );
        }

        var nodeTransition =
                nodeTransitionFactory.create(
                        nodeAdmission
                );

        if (nodeTransition.isEmpty()) {
            return new DirectionChainCommitResult(
                    false,
                    current,
                    "NODE_TRANSITION_REJECTED"
            );
        }

        var nodeCommit =
                nodeCommitService.commit(
                        nodeTransition.get(),
                        current
                );

        if (!nodeCommit.committed()) {
            return new DirectionChainCommitResult(
                    false,
                    current,
                    "NODE_COMMIT_REJECTED"
            );
        }

        var directionNode =
                nodeCommit.state()
                        .nodes()
                        .stream()
                        .filter(node ->
                                node.sourceProposalKey()
                                        .equals(
                                                nodeAdmission
                                                        .proposal()
                                                        .proposalKey()
                                        )
                        )
                        .findFirst()
                        .orElseThrow();

        var relationshipProposal =
                relationshipProposalFactory.create(
                        directionNode,
                        decision
                );

        var relationshipAdmission =
                relationshipAdmissionService.evaluate(
                        relationshipProposal,
                        new RelationshipValidationContext(
                                nodeCommit.state().nodes(),
                                nodeCommit.state()
                                        .relationships()
                        )
                );

        if (!relationshipAdmission.accepted()) {
            return new DirectionChainCommitResult(
                    false,
                    current,
                    "RELATIONSHIP_ADMISSION_REJECTED"
            );
        }

        var relationshipTransition =
                relationshipTransitionFactory.create(
                        relationshipAdmission
                );

        if (relationshipTransition.isEmpty()) {
            return new DirectionChainCommitResult(
                    false,
                    current,
                    "RELATIONSHIP_TRANSITION_REJECTED"
            );
        }

        var relationshipCommit =
                relationshipCommitService.commit(
                        relationshipTransition.get(),
                        nodeCommit.state()
                );

        if (!relationshipCommit.committed()) {
            return new DirectionChainCommitResult(
                    false,
                    current,
                    "RELATIONSHIP_COMMIT_REJECTED"
            );
        }

        return new DirectionChainCommitResult(
                true,
                relationshipCommit.state(),
                "COMMITTED"
        );
    }
}

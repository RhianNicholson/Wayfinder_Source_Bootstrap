package com.wayfinder.civilization.network;

import com.wayfinder.civilization.direction.DirectionDecision;
import com.wayfinder.civilization.service.ObservationDecision;
import com.wayfinder.civilization.target.CivilizationNodeTarget;
import com.wayfinder.civilization.target.GeographicNodeTarget;

public final class NodeProposalFactory {

    public NodeProposal fromObservationDecision(
            ObservationDecision decision
    ) {
        var selected = decision.selected();
        var candidate = selected.candidate();

        return new NodeProposal(
                "OBS:" + candidate.candidateKey(),
                decision.purpose(),
                candidate.position(),
                new GeographicNodeTarget(
                        candidate.target()
                ),
                selected.score().finalScore(),
                selected.reasons()
        );
    }

    public NodeProposal fromDirectionDecision(
            DirectionDecision decision
    ) {
        var selected = decision.selected();
        var candidate = selected.candidate();

        return new NodeProposal(
                candidate.candidateKey(),
                decision.purpose(),
                candidate.position(),
                new CivilizationNodeTarget(
                        candidate.destination().id()
                ),
                selected.score().finalScore(),
                selected.reasons()
        );
    }
}

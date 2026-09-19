package com.wayfinder.civilization.commit;

import com.wayfinder.civilization.network.NodeProposal;
import com.wayfinder.civilization.state.CivilizationNode;

public record NodeTransition(
        NodeProposal proposal,
        CivilizationNode node
) {}

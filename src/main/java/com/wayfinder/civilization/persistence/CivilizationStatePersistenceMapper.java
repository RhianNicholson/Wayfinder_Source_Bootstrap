package com.wayfinder.civilization.persistence;

import com.wayfinder.civilization.state.CivilizationState;

import java.util.List;

public final class CivilizationStatePersistenceMapper {
    private CivilizationStatePersistenceMapper() {}

    /**
     * Backward-compatible node mapping used by Milestone 9 tests/callers.
     */
    public static List<PersistedCivilizationNode> toPersisted(
            CivilizationState state
    ) {
        return state.nodes().stream()
                .map(PersistedCivilizationNode::fromDomain)
                .toList();
    }

    public static List<PersistedCivilizationRelationship>
            toPersistedRelationships(
                    CivilizationState state
            ) {

        return state.relationships().stream()
                .map(
                        PersistedCivilizationRelationship::fromDomain
                )
                .toList();
    }

    /**
     * Backward-compatible loader for Milestone 9 data that contains nodes only.
     */
    public static CivilizationState toDomain(
            List<PersistedCivilizationNode> nodes
    ) {
        return toDomain(
                nodes,
                List.of()
        );
    }

    public static CivilizationState toDomain(
            List<PersistedCivilizationNode> nodes,
            List<PersistedCivilizationRelationship> relationships
    ) {
        return new CivilizationState(
                nodes.stream()
                        .map(
                                PersistedCivilizationNode::toDomain
                        )
                        .toList(),
                relationships.stream()
                        .map(
                                PersistedCivilizationRelationship::toDomain
                        )
                        .toList()
        );
    }
}

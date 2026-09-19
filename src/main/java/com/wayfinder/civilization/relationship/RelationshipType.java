package com.wayfinder.civilization.relationship;

/**
 * Semantic relationships between committed civilization nodes.
 *
 * These describe what the Wayfinders intended nodes to mean to each other.
 */
public enum RelationshipType {
    /**
     * A DIRECTION node intentionally directs the traveler toward another node.
     *
     * First vertical-slice use:
     * DIRECTION -> OBSERVATION
     */
    DIRECTIONAL_REFERENCE,

    /**
     * An OBSERVATION node intentionally observes or references a target node.
     * Reserved for later node-to-node observation relationships.
     */
    OBSERVES,

    /**
     * A navigable route relationship between nodes.
     * Reserved for later travel-network work.
     */
    ROUTE
}

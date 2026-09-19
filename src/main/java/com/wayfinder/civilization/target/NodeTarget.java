package com.wayfinder.civilization.target;

/**
 * Authoritative semantic target of a civilization node.
 *
 * A node may point toward geography or toward another committed civilization
 * node. The distinction is historical truth and must never be inferred from a
 * placeholder object.
 */
public sealed interface NodeTarget
        permits GeographicNodeTarget, CivilizationNodeTarget {
}

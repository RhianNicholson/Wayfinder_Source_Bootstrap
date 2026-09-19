package com.wayfinder.structure.geometry;

/**
 * Local Shrine coordinates.
 *
 * right: positive toward the structure's local right side
 * up: vertical offset from the node origin
 * forward: positive toward the semantic target
 */
public record LocalStructurePosition(
        int right,
        int up,
        int forward
) {}

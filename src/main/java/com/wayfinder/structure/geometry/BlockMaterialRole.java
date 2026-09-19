package com.wayfinder.structure.geometry;

/**
 * Semantic material requirements used by geometry solvers.
 *
 * These are not Minecraft BlockStates. A later renderer may map these roles
 * into biome/era-appropriate palettes without changing the structure's
 * geometry or meaning.
 */
public enum BlockMaterialRole {
    FOUNDATION_STONE,
    PRIMARY_STONE,
    ACCENT_STONE,
    GLYPH_STONE,
    TOWER_FOUNDATION_STONE,
    TOWER_SUPPORT_STONE,
    PLATFORM_STONE,
    VIEW_ACCENT_STONE
}

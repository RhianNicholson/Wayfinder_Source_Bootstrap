package com.wayfinder.structure.model;

/**
 * Physical responsibilities that must be preserved by materialization.
 *
 * These are not decorative tags. Every planned component must serve at least
 * one explicit purpose.
 */
public enum StructurePurpose {
    COMMUNICATE_DIRECTION,
    SUPPORT_APPROACH,
    MARK_GLYPH_SURFACE,
    ENABLE_OBSERVATION,
    PROTECT_VIEW_ARC,
    SUPPORT_PLATFORM,
    ANCHOR_TO_TERRAIN
}

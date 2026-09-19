package com.wayfinder.structure.watchtower;

import com.wayfinder.structure.geometry.LocalStructurePosition;
import java.util.List;

/**
 * Canonical Wayfinder SIGHT glyph: △
 *
 * The three points form an upward triangle on the Watchtower's rear frame.
 * The target-facing side remains open.
 */
public final class SightGlyphPattern {
    private SightGlyphPattern() {}

    public static List<LocalStructurePosition> localCells(int platformUp) {
        return List.of(
                new LocalStructurePosition(-1, platformUp + 2, -2),
                new LocalStructurePosition(1, platformUp + 2, -2),
                new LocalStructurePosition(0, platformUp + 3, -2)
        );
    }
}

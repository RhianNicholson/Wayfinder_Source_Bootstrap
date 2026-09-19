package com.wayfinder.structure.shrine;

import com.wayfinder.structure.geometry.LocalStructurePosition;
import java.util.List;

/** Canonical four-stone diamond: WAY. Center remains open. */
public final class WayGlyphPattern {
    private WayGlyphPattern() {}
    public static List<LocalStructurePosition> localCells() {
        return List.of(
                new LocalStructurePosition(0, 1, -1),
                new LocalStructurePosition(-1, 2, -1),
                new LocalStructurePosition(1, 2, -1),
                new LocalStructurePosition(0, 3, -1)
        );
    }
}

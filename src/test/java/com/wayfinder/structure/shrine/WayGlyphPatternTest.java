package com.wayfinder.structure.shrine;

import com.wayfinder.structure.geometry.LocalStructurePosition;
import org.junit.jupiter.api.Test;
import java.util.Set;
import static org.junit.jupiter.api.Assertions.*;

final class WayGlyphPatternTest {
    @Test
    void approvedGlyphHasFourUniqueCellsAndOpenCenter() {
        var cells = WayGlyphPattern.localCells();
        assertEquals(4, cells.size());
        assertEquals(4, Set.copyOf(cells).size());
        assertFalse(cells.contains(new LocalStructurePosition(0, 2, -1)));
        assertTrue(cells.contains(new LocalStructurePosition(0, 1, -1)));
        assertTrue(cells.contains(new LocalStructurePosition(-1, 2, -1)));
        assertTrue(cells.contains(new LocalStructurePosition(1, 2, -1)));
        assertTrue(cells.contains(new LocalStructurePosition(0, 3, -1)));
    }
}

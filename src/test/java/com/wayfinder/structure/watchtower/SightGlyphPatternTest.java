package com.wayfinder.structure.watchtower;

import com.wayfinder.structure.geometry.LocalStructurePosition;
import org.junit.jupiter.api.Test;
import java.util.Set;
import static org.junit.jupiter.api.Assertions.*;

final class SightGlyphPatternTest {
    @Test
    void sightGlyphFormsThreePointTriangleOnRearFrame() {
        int platform = 6;
        var cells = SightGlyphPattern.localCells(platform);
        assertEquals(3, cells.size());
        assertEquals(3, Set.copyOf(cells).size());
        assertTrue(cells.contains(new LocalStructurePosition(-1, 8, -2)));
        assertTrue(cells.contains(new LocalStructurePosition(1, 8, -2)));
        assertTrue(cells.contains(new LocalStructurePosition(0, 9, -2)));
        assertTrue(cells.stream().allMatch(c -> c.forward() == -2));
    }
}

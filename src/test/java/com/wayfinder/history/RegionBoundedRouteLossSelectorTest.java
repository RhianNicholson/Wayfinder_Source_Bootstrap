package com.wayfinder.history;

import com.wayfinder.core.math.WorldPosition;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

final class RegionBoundedRouteLossSelectorTest {
    @Test
    void regionIdentitySeparatesBoundaryPositions() {
        var west = HistoricalRegionKey.from(new WorldPosition(511, 70, 0));
        var east = HistoricalRegionKey.from(new WorldPosition(512, 70, 0));
        assertNotEquals(west, east);
    }
}

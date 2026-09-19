package com.wayfinder.history;

import com.wayfinder.core.math.WorldPosition;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

final class HistoricalScopeTest {
    @Test
    void sameGeographicRegionProducesSameStableScope() {
        var a = HistoricalScope.at(new WorldPosition(10, 70, 10), 1, 1);
        var b = HistoricalScope.at(new WorldPosition(400, 90, 300), 1, 1);
        assertEquals(a.regionKey(), b.regionKey());
        assertEquals(a.eraKey(), b.eraKey());
    }

    @Test
    void crossingRegionBoundaryChangesRegionIdentity() {
        var a = HistoricalScope.at(new WorldPosition(511, 70, 0), 1, 1);
        var b = HistoricalScope.at(new WorldPosition(512, 70, 0), 1, 1);
        assertNotEquals(a.regionKey(), b.regionKey());
    }

    @Test
    void negativeCoordinatesUseFloorDivision() {
        var scope = HistoricalScope.at(new WorldPosition(-1, 70, -1), 2, 1);
        assertEquals("wayfinder-region:-1:-1", scope.regionKey());
        assertEquals("wayfinder-era:2", scope.eraKey());
    }
}

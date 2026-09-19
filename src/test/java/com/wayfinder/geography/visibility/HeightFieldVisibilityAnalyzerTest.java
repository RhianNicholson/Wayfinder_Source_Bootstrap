package com.wayfinder.geography.visibility;

import com.wayfinder.core.id.GeographicFeatureId;
import com.wayfinder.core.math.WorldPosition;
import com.wayfinder.geography.model.Landmark;
import com.wayfinder.geography.model.LandmarkType;
import com.wayfinder.geography.world.WorldTerrainView;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

final class HeightFieldVisibilityAnalyzerTest {

    @Test
    void clearTerrainProducesVisibleSightline() {
        WorldTerrainView terrain = new SyntheticView((x, z) -> 64);
        var analyzer = new HeightFieldVisibilityAnalyzer(terrain, 24);

        var target = landmark(new WorldPosition(100, 90, 0));
        var result = analyzer.analyze(new WorldPosition(0, 90, 0), target);

        assertTrue(result.visible());
        assertTrue(result.score() >= 0.75);
        assertTrue(result.obstruction() < 0.25);
    }

    @Test
    void interveningRidgeBlocksSightline() {
        WorldTerrainView terrain = new SyntheticView((x, z) ->
                x >= 40 && x <= 60 ? 120 : 64
        );
        var analyzer = new HeightFieldVisibilityAnalyzer(terrain, 40);

        var target = landmark(new WorldPosition(100, 90, 0));
        var result = analyzer.analyze(new WorldPosition(0, 90, 0), target);

        assertFalse(result.visible());
        assertTrue(result.score() < 0.75);
        assertTrue(result.obstruction() > 0.25);
    }

    @Test
    void isolatedNoiseDoesNotAutomaticallyDestroySightline() {
        WorldTerrainView terrain = new SyntheticView((x, z) ->
                x == 50 ? 95 : 64
        );
        var analyzer = new HeightFieldVisibilityAnalyzer(terrain, 40);

        var target = landmark(new WorldPosition(100, 90, 0));
        var result = analyzer.analyze(new WorldPosition(0, 90, 0), target);

        assertTrue(result.visible());
    }

    private static Landmark landmark(WorldPosition position) {
        return new Landmark(
                new GeographicFeatureId(
                        UUID.fromString("11111111-1111-1111-1111-111111111111")
                ),
                position,
                LandmarkType.PROMINENT_PEAK,
                0.9,
                0.9,
                0.8
        );
    }

    private record SyntheticView(HeightFunction height) implements WorldTerrainView {
        @Override
        public int surfaceHeight(int x, int z) {
            return height.at(x, z);
        }

        @Override
        public boolean isWater(int x, int y, int z) {
            return false;
        }

        @Override
        public boolean isSolid(int x, int y, int z) {
            return y <= surfaceHeight(x, z);
        }
    }

    @FunctionalInterface
    private interface HeightFunction {
        int at(int x, int z);
    }
}

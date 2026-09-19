package com.wayfinder.geography.analysis;

import com.wayfinder.core.math.WorldBounds;
import com.wayfinder.geography.model.TerrainSampleGrid;
import com.wayfinder.geography.world.WorldTerrainView;

public interface TerrainSampler {
    TerrainSampleGrid sample(WorldBounds bounds, int spacing, WorldTerrainView world);
}

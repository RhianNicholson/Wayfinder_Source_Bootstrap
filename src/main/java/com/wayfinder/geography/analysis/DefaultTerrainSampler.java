package com.wayfinder.geography.analysis;

import com.wayfinder.core.math.HorizontalPosition;
import com.wayfinder.core.math.WorldBounds;
import com.wayfinder.geography.model.TerrainSample;
import com.wayfinder.geography.model.TerrainSampleGrid;
import com.wayfinder.geography.world.WorldTerrainView;

import java.util.Objects;

public final class DefaultTerrainSampler implements TerrainSampler {
    @Override
    public TerrainSampleGrid sample(WorldBounds bounds, int spacing, WorldTerrainView world) {
        Objects.requireNonNull(bounds, "bounds");
        Objects.requireNonNull(world, "world");
        if (spacing <= 0) throw new IllegalArgumentException("spacing must be positive");

        int width = ((bounds.maxX() - bounds.minX()) / spacing) + 1;
        int depth = ((bounds.maxZ() - bounds.minZ()) / spacing) + 1;
        TerrainSampleGrid grid = new TerrainSampleGrid(spacing, width, depth);

        // Pass 1: acquire world facts only.
        for (int gx = 0; gx < width; gx++) {
            int x = bounds.minX() + gx * spacing;
            for (int gz = 0; gz < depth; gz++) {
                int z = bounds.minZ() + gz * spacing;
                int y = world.surfaceHeight(x, z);
                grid.setSample(gx, gz, new TerrainSample(
                    new HorizontalPosition(x, z), y, world.isWater(x, y, z), 0.0, 0.0
                ));
            }
        }

        // Pass 2: derive local terrain shape from immutable acquired heights.
        for (int gx = 0; gx < width; gx++) {
            for (int gz = 0; gz < depth; gz++) {
                TerrainSample center = grid.sampleAt(gx, gz);
                int minY = center.surfaceY();
                int maxY = center.surfaceY();
                int maxDifference = 0;

                int[][] offsets = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};
                for (int[] offset : offsets) {
                    int nx = gx + offset[0];
                    int nz = gz + offset[1];
                    if (nx < 0 || nz < 0 || nx >= width || nz >= depth) continue;
                    int neighborY = grid.sampleAt(nx, nz).surfaceY();
                    minY = Math.min(minY, neighborY);
                    maxY = Math.max(maxY, neighborY);
                    maxDifference = Math.max(maxDifference, Math.abs(neighborY - center.surfaceY()));
                }

                double slope = Math.min(1.0, maxDifference / (double) spacing);
                double roughness = Math.min(1.0, (maxY - minY) / (double) (spacing * 2));
                grid.setSample(gx, gz, new TerrainSample(
                    center.position(), center.surfaceY(), center.water(), slope, roughness
                ));
            }
        }

        return grid;
    }
}

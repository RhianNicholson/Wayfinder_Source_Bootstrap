package com.wayfinder.geography.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class TerrainSampleGrid {
    private final int spacing;
    private final int width;
    private final int depth;
    private final TerrainSample[][] samples;

    public TerrainSampleGrid(int spacing, int width, int depth) {
        if (spacing <= 0 || width <= 0 || depth <= 0) {
            throw new IllegalArgumentException("spacing, width, and depth must be positive");
        }
        this.spacing = spacing;
        this.width = width;
        this.depth = depth;
        this.samples = new TerrainSample[width][depth];
    }

    public int spacing() { return spacing; }
    public int width() { return width; }
    public int depth() { return depth; }

    public TerrainSample sampleAt(int gridX, int gridZ) {
        return samples[gridX][gridZ];
    }

    public void setSample(int gridX, int gridZ, TerrainSample sample) {
        samples[gridX][gridZ] = sample;
    }

    public List<TerrainSample> samples() {
        List<TerrainSample> result = new ArrayList<>(width * depth);
        for (int x = 0; x < width; x++) {
            for (int z = 0; z < depth; z++) {
                if (samples[x][z] != null) result.add(samples[x][z]);
            }
        }
        return Collections.unmodifiableList(result);
    }
}

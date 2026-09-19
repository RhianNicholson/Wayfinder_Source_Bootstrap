package com.wayfinder.geography.world;

public interface WorldTerrainView {
    int surfaceHeight(int x, int z);
    boolean isWater(int x, int y, int z);
    boolean isSolid(int x, int y, int z);
}

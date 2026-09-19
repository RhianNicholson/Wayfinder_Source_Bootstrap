package com.wayfinder.testsupport;

import com.wayfinder.geography.world.WorldTerrainView;
import java.util.Objects;
import java.util.function.IntBinaryOperator;

public final class SyntheticTerrainView implements WorldTerrainView {
    private final IntBinaryOperator heightFunction;

    public SyntheticTerrainView(IntBinaryOperator heightFunction) {
        this.heightFunction = Objects.requireNonNull(heightFunction, "heightFunction");
    }

    @Override
    public int surfaceHeight(int x, int z) {
        return heightFunction.applyAsInt(x, z);
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

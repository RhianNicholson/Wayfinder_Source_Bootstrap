package com.wayfinder.minecraft.world;

import com.wayfinder.geography.world.WorldTerrainView;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.levelgen.Heightmap;

import java.util.Objects;

/**
 * Minecraft/NeoForge adapter for terrain facts consumed by the loader-neutral
 * Wayfinder geography engine.
 */
public final class NeoForgeWorldTerrainView implements WorldTerrainView {
    private final ServerLevel level;

    public NeoForgeWorldTerrainView(ServerLevel level) {
        this.level = Objects.requireNonNull(level, "level");
    }

    @Override
    public int surfaceHeight(int x, int z) {
        return level.getHeight(Heightmap.Types.WORLD_SURFACE, x, z);
    }

    @Override
    public boolean isWater(int x, int y, int z) {
        return level.getFluidState(new BlockPos(x, y, z)).isSource()
            && !level.getFluidState(new BlockPos(x, y, z)).isEmpty();
    }

    @Override
    public boolean isSolid(int x, int y, int z) {
        BlockPos pos = new BlockPos(x, y, z);
        return level.getBlockState(pos).isCollisionShapeFullBlock(level, pos);
    }
}

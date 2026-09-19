package com.wayfinder.minecraft.structure;

import com.wayfinder.structure.geometry.BlockMaterialRole;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Minecraft palette owned only by the Watchtower renderer.
 */
public final class WatchtowerBlockPalette {

    public BlockState stateFor(
            BlockMaterialRole role
    ) {
        return switch (role) {
            case TOWER_FOUNDATION_STONE ->
                    Blocks.COBBLESTONE.defaultBlockState();
            case TOWER_SUPPORT_STONE ->
                    Blocks.STONE_BRICKS.defaultBlockState();
            case PLATFORM_STONE ->
                    Blocks.SMOOTH_STONE.defaultBlockState();
            case VIEW_ACCENT_STONE ->
                    Blocks.POLISHED_ANDESITE.defaultBlockState();

            case FOUNDATION_STONE,
                 PRIMARY_STONE,
                 ACCENT_STONE,
                 GLYPH_STONE ->
                    throw new IllegalArgumentException(
                            "Shrine material role cannot be rendered by WatchtowerBlockPalette: "
                                    + role
                    );
        };
    }
}

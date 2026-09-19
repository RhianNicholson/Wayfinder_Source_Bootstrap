package com.wayfinder.minecraft.structure;

import com.wayfinder.structure.geometry.BlockMaterialRole;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Minecraft-side palette for the Waystone Shrine only.
 *
 * Milestone 19 expanded BlockMaterialRole with Watchtower-specific roles.
 * This Shrine palette rejects non-Shrine roles explicitly.
 */
public final class WaystoneShrineBlockPalette {

    public BlockState stateFor(BlockMaterialRole role) {
        return switch (role) {
            case FOUNDATION_STONE -> Blocks.COBBLESTONE.defaultBlockState();
            case PRIMARY_STONE -> Blocks.STONE_BRICKS.defaultBlockState();
            case ACCENT_STONE -> Blocks.POLISHED_ANDESITE.defaultBlockState();
            case GLYPH_STONE -> Blocks.CHISELED_STONE_BRICKS.defaultBlockState();

            case TOWER_FOUNDATION_STONE,
                 TOWER_SUPPORT_STONE,
                 PLATFORM_STONE,
                 VIEW_ACCENT_STONE ->
                    throw new IllegalArgumentException(
                            "Watchtower material role cannot be rendered by WaystoneShrineBlockPalette: "
                                    + role
                    );
        };
    }
}

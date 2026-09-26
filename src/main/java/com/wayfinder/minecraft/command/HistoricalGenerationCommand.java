package com.wayfinder.minecraft.command;

import com.mojang.brigadier.context.CommandContext;
import com.wayfinder.core.math.WorldPosition;
import com.wayfinder.minecraft.history.MinecraftHistoricalScopeResolver;
import com.wayfinder.minecraft.history.MinecraftHistoricalGenerationOrchestrator;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;

import java.util.Locale;

public final class HistoricalGenerationCommand {
    private HistoricalGenerationCommand() {}

    public static int execute(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        ServerLevel level = source.getLevel();

        var pos = source.getPosition();
        var position = new WorldPosition(
                (int) Math.floor(pos.x()),
                (int) Math.floor(pos.y()),
                (int) Math.floor(pos.z()));

        var scope = new MinecraftHistoricalScopeResolver()
                .resolve(level, position);

        var result = new MinecraftHistoricalGenerationOrchestrator()
                .run(level, scope);

        source.sendSuccess(() -> Component.literal(String.format(
                Locale.ROOT,
                "Wayfinder history | region=%s | era=%s | eventCommitted=%s | physicalized=%s | removedBlocks=%d | roll=%.4f | threshold=%.4f",
                scope.regionKey(),
                scope.eraKey(),
                result.eventCommitted(),
                result.physicalized(),
                result.removedBlocks(),
                result.roll(),
                result.threshold()
        )), false);

        return 1;
    }
}

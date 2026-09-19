package com.wayfinder.minecraft.command;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.wayfinder.minecraft.persistence.MinecraftSavedDataMaterializationRepository;
import com.wayfinder.minecraft.structure.MinecraftMaterializationBlockView;
import com.wayfinder.structure.materialization.MaterializationCommitService;
import com.wayfinder.structure.materialization.MaterializationInspector;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;

import java.util.Comparator;
import java.util.Locale;

public final class WayfinderMaterializationStatusCommand {
    private WayfinderMaterializationStatusCommand() {}

    public static int execute(
            CommandContext<CommandSourceStack> context
    ) throws CommandSyntaxException {
        var source = context.getSource();
        var player = source.getPlayerOrException();
        var level = source.getLevel();

        var repository =
                new MinecraftSavedDataMaterializationRepository(
                        level.getServer()
                );

        var state = repository.load();

        if (state.records().isEmpty()) {
            source.sendSuccess(
                    () -> Component.literal(
                            "Wayfinder materialization: no persistent physical records exist yet."
                    ),
                    false
            );
            return 1;
        }

        var playerPos =
                new com.wayfinder.core.math.WorldPosition(
                        player.blockPosition().getX(),
                        player.blockPosition().getY(),
                        player.blockPosition().getZ()
                );

        var nearest =
                state.records()
                        .stream()
                        .min(
                                Comparator.comparingDouble(
                                        record ->
                                                record.originalCells()
                                                        .get(0)
                                                        .position()
                                                        .horizontalDistanceTo(
                                                                playerPos
                                                        )
                                )
                        )
                        .orElseThrow();

        var inspection =
                new MaterializationInspector()
                        .inspect(
                                nearest,
                                new MinecraftMaterializationBlockView(level)
                        );

        if (inspection.condition() != nearest.condition()) {
            var update =
                    new MaterializationCommitService()
                            .updateCondition(
                                    state,
                                    nearest,
                                    inspection.condition()
                            );

            if (update.committed()) {
                repository.save(update.state());
            }
        }

        source.sendSuccess(
                () -> Component.literal(
                        String.format(
                                Locale.ROOT,
                                "Wayfinder materialization: %s | node %s | archetype %s | integrity %d/%d (%.0f%%) | format v%d | palette v%d",
                                inspection.condition(),
                                nearest.sourceNodeId().value(),
                                nearest.archetype(),
                                inspection.matchingCells(),
                                inspection.totalCells(),
                                inspection.integrity() * 100.0,
                                nearest.formatVersion(),
                                nearest.paletteVersion()
                        )
                ),
                false
        );

        return 1;
    }
}

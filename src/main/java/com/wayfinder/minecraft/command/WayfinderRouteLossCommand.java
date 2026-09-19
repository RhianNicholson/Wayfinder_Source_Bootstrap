package com.wayfinder.minecraft.command;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.wayfinder.history.HistoricalEventCommitService;
import com.wayfinder.history.HistoricalEventFactory;
import com.wayfinder.history.RouteLossTransitionService;
import com.wayfinder.minecraft.history.MinecraftRouteLossApplier;
import com.wayfinder.minecraft.persistence.MinecraftSavedDataHistoricalEventRepository;
import com.wayfinder.minecraft.persistence.MinecraftSavedDataMaterializationRepository;
import com.wayfinder.structure.materialization.MaterializationCondition;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;

import java.util.Comparator;
import java.util.Locale;

public final class WayfinderRouteLossCommand {
    private WayfinderRouteLossCommand() {}

    public static int execute(
            CommandContext<CommandSourceStack> context
    ) throws CommandSyntaxException {
        var source = context.getSource();
        var player = source.getPlayerOrException();
        var level = source.getLevel();

        var materializationRepository =
                new MinecraftSavedDataMaterializationRepository(
                        level.getServer()
                );
        var historicalRepository =
                new MinecraftSavedDataHistoricalEventRepository(
                        level.getServer()
                );

        var materializationState =
                materializationRepository.load();
        var historicalState =
                historicalRepository.load();

        if (materializationState.records().isEmpty()) {
            source.sendSuccess(
                    () -> Component.literal(
                            "Wayfinder route loss: no recorded materializations exist."
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
                materializationState.records()
                        .stream()
                        .filter(record ->
                                record.condition()
                                        != MaterializationCondition.LOST
                        )
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
                        );

        if (nearest.isEmpty()) {
            source.sendSuccess(
                    () -> Component.literal(
                            "Wayfinder route loss: no non-lost materialization exists."
                    ),
                    false
            );
            return 1;
        }

        var target = nearest.get();

        var transitionService =
                new RouteLossTransitionService();

        var validation =
                transitionService.validate(
                        historicalState,
                        materializationState,
                        target.sourceNodeId()
                );

        if (!validation.valid()) {
            source.sendSuccess(
                    () -> Component.literal(
                            "Wayfinder route loss: REFUSED | "
                                    + validation.reason()
                                    + " | NO WORLD CHANGES"
                    ),
                    false
            );
            return 1;
        }

        var event =
                new HistoricalEventFactory()
                        .routeLoss(
                                historicalState,
                                target.sourceNodeId(),
                                "historical route loss"
                        );

        var eventCommit =
                new HistoricalEventCommitService()
                        .commit(
                                historicalState,
                                event
                        );

        if (!eventCommit.committed()) {
            source.sendSuccess(
                    () -> Component.literal(
                            "Wayfinder route loss: EVENT COMMIT FAILED | NO WORLD CHANGES"
                    ),
                    false
            );
            return 1;
        }

        /*
         * Event truth is persisted before physical mutation. If the game were
         * to stop after this point, history would still correctly say that a
         * route-loss event occurred.
         */
        historicalRepository.save(
                eventCommit.state()
        );

        int removed =
                new MinecraftRouteLossApplier()
                        .removeRecordedStructure(
                                level,
                                target
                        );

        var lostState =
                transitionService.markLost(
                        materializationState,
                        target
                );

        materializationRepository.save(
                lostState
        );

        source.sendSuccess(
                () -> Component.literal(
                        String.format(
                                Locale.ROOT,
                                "Wayfinder route loss: APPLIED | node %s | archetype %s | removed %d recorded blocks | condition LOST | civilization truth preserved",
                                target.sourceNodeId().value(),
                                target.archetype(),
                                removed
                        )
                ),
                false
        );

        return 1;
    }
}

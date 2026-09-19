package com.wayfinder.minecraft.command;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.wayfinder.history.BrokenContinuationSelector;
import com.wayfinder.history.HistoricalEventCommitService;
import com.wayfinder.history.HistoricalEventFactory;
import com.wayfinder.history.RouteLossTransitionService;
import com.wayfinder.minecraft.history.MinecraftRouteLossApplier;
import com.wayfinder.minecraft.persistence.MinecraftSavedDataCivilizationRepository;
import com.wayfinder.minecraft.persistence.MinecraftSavedDataHistoricalEventRepository;
import com.wayfinder.minecraft.persistence.MinecraftSavedDataMaterializationRepository;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import java.util.Locale;

public final class WayfinderBreakContinuationCommand {
    private WayfinderBreakContinuationCommand() {}

    public static int execute(CommandContext<CommandSourceStack> context)
            throws CommandSyntaxException {
        var source = context.getSource();
        var level = source.getLevel();

        var civRepo = new MinecraftSavedDataCivilizationRepository(level.getServer());
        var matRepo = new MinecraftSavedDataMaterializationRepository(level.getServer());
        var historyRepo = new MinecraftSavedDataHistoricalEventRepository(level.getServer());

        var civilization = civRepo.load();
        var materialization = matRepo.load();
        var history = historyRepo.load();

        var continuation = new BrokenContinuationSelector()
                .select(civilization, materialization);

        if (continuation.isEmpty()) {
            source.sendSuccess(() -> Component.literal(
                    "Wayfinder broken continuation: no eligible committed DIRECTION -> OBSERVATION continuation exists. NO WORLD CHANGES"), false);
            return 1;
        }

        var selected = continuation.get();
        var target = selected.targetMaterialization();
        var transition = new RouteLossTransitionService();
        var validation = transition.validate(history, materialization, target.sourceNodeId());

        if (!validation.valid()) {
            source.sendSuccess(() -> Component.literal(
                    "Wayfinder broken continuation: REFUSED | " + validation.reason()
                            + " | NO WORLD CHANGES"), false);
            return 1;
        }

        var event = new HistoricalEventFactory().routeLoss(
                history, target.sourceNodeId(), "broken directional continuation");
        var committed = new HistoricalEventCommitService().commit(history, event);

        if (!committed.committed()) {
            source.sendSuccess(() -> Component.literal(
                    "Wayfinder broken continuation: historical event commit failed. NO WORLD CHANGES"), false);
            return 1;
        }

        historyRepo.save(committed.state());

        int removed = new MinecraftRouteLossApplier()
                .removeRecordedStructure(level, target);

        matRepo.save(transition.markLost(materialization, target));

        source.sendSuccess(() -> Component.literal(String.format(
                Locale.ROOT,
                "Wayfinder broken continuation: APPLIED | source node %s still points via %s to lost node %s | removed %d surviving recorded blocks | relationship preserved",
                selected.sourceNode().id().value(),
                selected.relationship().type(),
                selected.lostTargetNode().id().value(),
                removed)), false);
        return 1;
    }
}

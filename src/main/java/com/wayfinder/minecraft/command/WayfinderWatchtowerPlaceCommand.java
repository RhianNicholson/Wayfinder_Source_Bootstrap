package com.wayfinder.minecraft.command;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.wayfinder.civilization.state.CivilizationState;
import com.wayfinder.minecraft.persistence.MinecraftSavedDataCivilizationRepository;
import com.wayfinder.minecraft.persistence.MinecraftSavedDataMaterializationRepository;
import com.wayfinder.minecraft.structure.WatchtowerBlockPalette;
import com.wayfinder.minecraft.structure.WatchtowerWorldRenderer;
import com.wayfinder.minecraft.world.NeoForgeWorldTerrainView;
import com.wayfinder.structure.model.StructureArchetype;
import com.wayfinder.structure.placement.StructurePlacementGuard;
import com.wayfinder.structure.service.CivilizationStructurePlanningService;
import com.wayfinder.structure.service.StructureIntentFactory;
import com.wayfinder.structure.service.StructureMaterializationPlanner;
import com.wayfinder.structure.watchtower.WatchtowerGeometrySolver;
import com.wayfinder.structure.watchtower.WatchtowerSightlineSolver;
import com.wayfinder.structure.watchtower.site.WatchtowerSitePlanningService;
import com.wayfinder.structure.watchtower.site.WatchtowerSiteValidator;
import com.wayfinder.structure.watchtower.site.WatchtowerTerrainAdapter;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;

import java.util.Comparator;
import java.util.Locale;

public final class WayfinderWatchtowerPlaceCommand {
    private WayfinderWatchtowerPlaceCommand() {}

    public static int execute(
            CommandContext<CommandSourceStack> context
    ) throws CommandSyntaxException {
        var source = context.getSource();
        var player = source.getPlayerOrException();
        var level = source.getLevel();
        var world = new NeoForgeWorldTerrainView(level);

        CivilizationState state =
                new MinecraftSavedDataCivilizationRepository(
                        level.getServer()
                ).load();

        var planning =
                new CivilizationStructurePlanningService(
                        new StructureIntentFactory(),
                        new StructureMaterializationPlanner()
                );

        var playerPosition =
                new com.wayfinder.core.math.WorldPosition(
                        player.blockPosition().getX(),
                        player.blockPosition().getY(),
                        player.blockPosition().getZ()
                );

        var nearest =
                planning.plan(state)
                        .stream()
                        .filter(plan ->
                                plan.intent().archetype()
                                        == StructureArchetype.WATCHTOWER
                        )
                        .min(
                                Comparator.comparingDouble(
                                        plan ->
                                                plan.intent()
                                                        .origin()
                                                        .horizontalDistanceTo(
                                                                playerPosition
                                                        )
                                )
                        );

        if (nearest.isEmpty()) {
            source.sendSuccess(
                    () -> Component.literal(
                            "Wayfinder Watchtower placement: no committed Watchtower intent exists."
                    ),
                    false
            );
            return 1;
        }

        var sightlineSolver =
                new WatchtowerSightlineSolver(
                        32,
                        6,
                        24,
                        0.75
                );

        var solved =
                new WatchtowerGeometrySolver(
                        sightlineSolver
                ).solve(
                        nearest.get(),
                        world
                );

        var intent = nearest.get().intent();

        if (solved.isEmpty()) {
            source.sendSuccess(
                    () -> Component.literal(
                            "Wayfinder Watchtower placement: NO FUNCTIONAL SIGHTLINE | NO WORLD CHANGES"
                    ),
                    false
            );
            return 1;
        }

        var site =
                new WatchtowerSitePlanningService(
                        new WatchtowerSiteValidator(
                                3,
                                sightlineSolver
                        ),
                        new WatchtowerTerrainAdapter()
                ).evaluate(
                        solved.get(),
                        world
                );

        if (!site.validation().accepted()) {
            String issues =
                    site.validation()
                            .issues()
                            .stream()
                            .map(issue ->
                                    issue.code().name()
                            )
                            .reduce(
                                    (a, b) -> a + "," + b
                            )
                            .orElse("UNKNOWN");

            source.sendSuccess(
                    () -> Component.literal(
                            String.format(
                                    Locale.ROOT,
                                    "Wayfinder Watchtower placement: SITE REJECTED | node %s | issues %s | NO WORLD CHANGES",
                                    intent.sourceNodeId().value(),
                                    issues
                            )
                    ),
                    false
            );
            return 1;
        }

        var adapted =
                site.adapted().orElseThrow();

        var result =
                new WatchtowerWorldRenderer(
                        new WatchtowerBlockPalette(),
                        new StructurePlacementGuard()
                ).place(
                        level,
                        adapted.geometry()
                );

        if (result.decision().status()
                        == com.wayfinder.structure.placement.StructurePlacementStatus.READY
                || result.decision().status()
                        == com.wayfinder.structure.placement.StructurePlacementStatus.ALREADY_PRESENT) {
            var materializationRepository =
                    new MinecraftSavedDataMaterializationRepository(
                            level.getServer()
                    );

            var materializationState =
                    materializationRepository.load();

            var recordResult =
                    new com.wayfinder.structure.materialization.MaterializationPlacementRecorder(
                            new com.wayfinder.structure.materialization.MaterializationRecordFactory(),
                            new com.wayfinder.structure.materialization.MaterializationCommitService()
                    ).record(
                            materializationState,
                            adapted.geometry()
                    );

            if (recordResult.committed()) {
                materializationRepository.save(
                        recordResult.state()
                );
            }
        }

        String message =
                switch (result.decision().status()) {
                    case READY ->
                            String.format(
                                    Locale.ROOT,
                                    "Wayfinder Watchtower placement: PLACED | node %s | origin [%d,%d,%d] | facing %s | platformY %d | height %d | changed %d blocks",
                                    intent.sourceNodeId().value(),
                                    intent.origin().x(),
                                    intent.origin().y(),
                                    intent.origin().z(),
                                    intent.facing(),
                                    adapted.sightline().platformY(),
                                    adapted.sightline().towerHeight(),
                                    result.changedBlocks()
                            );

                    case ALREADY_PRESENT ->
                            String.format(
                                    Locale.ROOT,
                                    "Wayfinder Watchtower placement: ALREADY PRESENT | node %s | changed 0 blocks",
                                    intent.sourceNodeId().value()
                            );

                    case PARTIAL_EXISTING_STRUCTURE ->
                            String.format(
                                    Locale.ROOT,
                                    "Wayfinder Watchtower placement: REFUSED PARTIAL STRUCTURE | node %s | matching %d | NO REPAIR PERFORMED",
                                    intent.sourceNodeId().value(),
                                    result.decision().matchingBlocks()
                            );

                    case BLOCKED ->
                            String.format(
                                    Locale.ROOT,
                                    "Wayfinder Watchtower placement: BLOCKED | node %s | blocked %d | NO WORLD CHANGES",
                                    intent.sourceNodeId().value(),
                                    result.decision().blockedBlocks()
                            );
                };

        source.sendSuccess(
                () -> Component.literal(message),
                false
        );

        return 1;
    }
}

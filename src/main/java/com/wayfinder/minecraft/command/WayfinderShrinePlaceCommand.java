package com.wayfinder.minecraft.command;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.wayfinder.civilization.state.CivilizationState;
import com.wayfinder.minecraft.persistence.MinecraftSavedDataCivilizationRepository;
import com.wayfinder.minecraft.persistence.MinecraftSavedDataMaterializationRepository;
import com.wayfinder.minecraft.structure.WaystoneShrineBlockPalette;
import com.wayfinder.minecraft.structure.WaystoneShrineWorldRenderer;
import com.wayfinder.minecraft.world.NeoForgeWorldTerrainView;
import com.wayfinder.structure.model.StructureArchetype;
import com.wayfinder.structure.placement.StructurePlacementGuard;
import com.wayfinder.structure.service.CivilizationStructurePlanningService;
import com.wayfinder.structure.service.StructureIntentFactory;
import com.wayfinder.structure.service.StructureMaterializationPlanner;
import com.wayfinder.structure.shrine.WaystoneShrineGeometrySolver;
import com.wayfinder.structure.site.WaystoneShrineSitePlanningService;
import com.wayfinder.structure.site.WaystoneShrineSiteValidator;
import com.wayfinder.structure.site.WaystoneShrineTerrainAdapter;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;

import java.util.Comparator;
import java.util.Locale;

public final class WayfinderShrinePlaceCommand {
    private WayfinderShrinePlaceCommand() {}

    public static int execute(
            CommandContext<CommandSourceStack> context
    ) throws CommandSyntaxException {
        var source = context.getSource();
        var player = source.getPlayerOrException();
        var level = source.getLevel();

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

        var nearestShrine =
                planning.plan(state)
                        .stream()
                        .filter(plan ->
                                plan.intent().archetype()
                                        == StructureArchetype.WAYSTONE_SHRINE
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

        if (nearestShrine.isEmpty()) {
            source.sendSuccess(
                    () -> Component.literal(
                            "Wayfinder Shrine placement: no committed Shrine intent exists."
                    ),
                    false
            );
            return 1;
        }

        var geometry =
                new WaystoneShrineGeometrySolver()
                        .solve(nearestShrine.get());

        var site =
                new WaystoneShrineSitePlanningService(
                        new WaystoneShrineSiteValidator(
                                3,
                                1,
                                3
                        ),
                        new WaystoneShrineTerrainAdapter()
                ).evaluate(
                        geometry,
                        new NeoForgeWorldTerrainView(level)
                );

        var intent = nearestShrine.get().intent();

        if (!site.validation().accepted()) {
            String issues =
                    site.validation()
                            .issues()
                            .stream()
                            .map(issue -> issue.code().name())
                            .reduce(
                                    (a, b) -> a + "," + b
                            )
                            .orElse("UNKNOWN");

            source.sendSuccess(
                    () -> Component.literal(
                            String.format(
                                    Locale.ROOT,
                                    "Wayfinder Shrine placement: SITE REJECTED | origin [%d,%d,%d] | issues %s | NO WORLD CHANGES",
                                    intent.origin().x(),
                                    intent.origin().y(),
                                    intent.origin().z(),
                                    issues
                            )
                    ),
                    false
            );
            return 1;
        }

        var adapted = site.adapted().orElseThrow();

        var result =
                new WaystoneShrineWorldRenderer(
                        new WaystoneShrineBlockPalette(),
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
                                    "Wayfinder Shrine placement: PLACED | node %s | origin [%d,%d,%d] | facing %s | changed %d blocks",
                                    intent.sourceNodeId().value(),
                                    intent.origin().x(),
                                    intent.origin().y(),
                                    intent.origin().z(),
                                    intent.facing(),
                                    result.changedBlocks()
                            );

                    case ALREADY_PRESENT ->
                            String.format(
                                    Locale.ROOT,
                                    "Wayfinder Shrine placement: ALREADY PRESENT | node %s | origin [%d,%d,%d] | changed 0 blocks",
                                    intent.sourceNodeId().value(),
                                    intent.origin().x(),
                                    intent.origin().y(),
                                    intent.origin().z()
                            );

                    case PARTIAL_EXISTING_STRUCTURE ->
                            String.format(
                                    Locale.ROOT,
                                    "Wayfinder Shrine placement: REFUSED PARTIAL STRUCTURE | node %s | matching %d | NO REPAIR PERFORMED",
                                    intent.sourceNodeId().value(),
                                    result.decision().matchingBlocks()
                            );

                    case BLOCKED ->
                            String.format(
                                    Locale.ROOT,
                                    "Wayfinder Shrine placement: BLOCKED | node %s | blocked %d | NO WORLD CHANGES",
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

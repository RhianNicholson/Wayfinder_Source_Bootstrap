package com.wayfinder.minecraft.command;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.wayfinder.civilization.state.CivilizationState;
import com.wayfinder.minecraft.persistence.MinecraftSavedDataCivilizationRepository;
import com.wayfinder.minecraft.world.NeoForgeWorldTerrainView;
import com.wayfinder.structure.model.StructureArchetype;
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

/**
 * Read-only live Watchtower preview.
 */
public final class WayfinderWatchtowerPreviewCommand {
    private WayfinderWatchtowerPreviewCommand() {}

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
                            "Wayfinder Watchtower preview: no committed Watchtower intent exists."
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
                            String.format(
                                    Locale.ROOT,
                                    "Wayfinder Watchtower preview: NO FUNCTIONAL SIGHTLINE | node %s | origin [%d,%d,%d] | target [%d,%d,%d] | NO WORLD CHANGES",
                                    intent.sourceNodeId().value(),
                                    intent.origin().x(),
                                    intent.origin().y(),
                                    intent.origin().z(),
                                    intent.semanticTarget().x(),
                                    intent.semanticTarget().y(),
                                    intent.semanticTarget().z()
                            )
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
                                    "Wayfinder Watchtower preview: REJECTED | node %s | origin [%d,%d,%d] | facing %s | platformY %d | towerHeight %d | issues %s | NO WORLD CHANGES",
                                    intent.sourceNodeId().value(),
                                    intent.origin().x(),
                                    intent.origin().y(),
                                    intent.origin().z(),
                                    intent.facing(),
                                    solved.get().sightline().platformY(),
                                    solved.get().sightline().towerHeight(),
                                    issues
                            )
                    ),
                    false
            );
            return 1;
        }

        var adapted =
                site.adapted().orElseThrow();

        source.sendSuccess(
                () -> Component.literal(
                        String.format(
                                Locale.ROOT,
                                "Wayfinder Watchtower preview: ACCEPTED | node %s | origin [%d,%d,%d] | facing %s | platformY %d | towerHeight %d | sightline %.2f | blocks %d | support +%d | NO WORLD CHANGES",
                                intent.sourceNodeId().value(),
                                intent.origin().x(),
                                intent.origin().y(),
                                intent.origin().z(),
                                intent.facing(),
                                adapted.sightline().platformY(),
                                adapted.sightline().towerHeight(),
                                adapted.sightline().visibilityScore(),
                                adapted.geometry().blocks().size(),
                                adapted.addedSupportBlocks()
                        )
                ),
                false
        );

        return 1;
    }
}

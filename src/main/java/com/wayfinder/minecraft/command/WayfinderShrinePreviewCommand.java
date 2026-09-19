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
import com.wayfinder.structure.shrine.WaystoneShrineGeometrySolver;
import com.wayfinder.structure.site.WaystoneShrineSitePlanningService;
import com.wayfinder.structure.site.WaystoneShrineSiteValidator;
import com.wayfinder.structure.site.WaystoneShrineTerrainAdapter;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;

import java.util.Comparator;
import java.util.Locale;

/**
 * Read-only in-game Shrine preview.
 *
 * No blocks, entities, particles, or civilization state are mutated.
 */
public final class WayfinderShrinePreviewCommand {
    private WayfinderShrinePreviewCommand() {}

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

        var nearestShrine =
                planning.plan(state)
                        .stream()
                        .filter(plan ->
                                plan.intent()
                                        .archetype()
                                        == StructureArchetype.WAYSTONE_SHRINE
                        )
                        .min(
                                Comparator.comparingDouble(
                                        plan ->
                                                plan.intent()
                                                        .origin()
                                                        .horizontalDistanceTo(
                                                                new com.wayfinder.core.math.WorldPosition(
                                                                        player.blockPosition().getX(),
                                                                        player.blockPosition().getY(),
                                                                        player.blockPosition().getZ()
                                                                )
                                                        )
                                )
                        );

        if (nearestShrine.isEmpty()) {
            source.sendSuccess(
                    () -> Component.literal(
                            "Wayfinder Shrine preview: no committed Shrine intent exists."
                    ),
                    false
            );
            return 1;
        }

        var geometry =
                new WaystoneShrineGeometrySolver()
                        .solve(
                                nearestShrine.get()
                        );

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
                        new NeoForgeWorldTerrainView(
                                level
                        )
                );

        var intent =
                nearestShrine.get()
                        .intent();

        if (!site.validation()
                .accepted()) {
            String issues =
                    site.validation()
                            .issues()
                            .stream()
                            .map(issue ->
                                    issue.code()
                                            .name()
                            )
                            .reduce(
                                    (a, b) ->
                                            a + "," + b
                            )
                            .orElse("UNKNOWN");

            source.sendSuccess(
                    () -> Component.literal(
                            String.format(
                                    Locale.ROOT,
                                    "Wayfinder Shrine preview: REJECTED | node %s | origin [%d,%d,%d] | facing %s | terrain spread %d | issues %s",
                                    intent.sourceNodeId()
                                            .value(),
                                    intent.origin().x(),
                                    intent.origin().y(),
                                    intent.origin().z(),
                                    intent.facing(),
                                    site.validation()
                                            .terrainSpread(),
                                    issues
                            )
                    ),
                    false
            );

            return 1;
        }

        var adapted =
                site.adapted()
                        .orElseThrow();

        source.sendSuccess(
                () -> Component.literal(
                        String.format(
                                Locale.ROOT,
                                "Wayfinder Shrine preview: ACCEPTED | node %s | origin [%d,%d,%d] | facing %s | blocks %d | foundation +%d | terrain spread %d | NO WORLD CHANGES",
                                intent.sourceNodeId()
                                        .value(),
                                intent.origin().x(),
                                intent.origin().y(),
                                intent.origin().z(),
                                intent.facing(),
                                adapted.geometry()
                                        .blocks()
                                        .size(),
                                adapted.addedFoundationBlocks(),
                                site.validation()
                                        .terrainSpread()
                        )
                ),
                false
        );

        return 1;
    }
}

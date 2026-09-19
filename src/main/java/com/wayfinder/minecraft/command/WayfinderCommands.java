package com.wayfinder.minecraft.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.wayfinder.WayfinderMod;
import com.wayfinder.civilization.candidate.ObservationCandidateGenerator;
import com.wayfinder.civilization.candidate.ObservationCandidateValidator;
import com.wayfinder.civilization.commit.NodeCommitService;
import com.wayfinder.civilization.commit.NodeTransitionFactory;
import com.wayfinder.civilization.commit.NodeTransitionValidator;
import com.wayfinder.civilization.direction.DeterministicDirectionSelector;
import com.wayfinder.civilization.direction.DirectionCandidateGenerator;
import com.wayfinder.civilization.direction.DirectionCandidateScorer;
import com.wayfinder.civilization.direction.DirectionCandidateValidator;
import com.wayfinder.civilization.direction.DirectionChainCommitService;
import com.wayfinder.civilization.direction.DirectionDecision;
import com.wayfinder.civilization.direction.DirectionDecisionService;
import com.wayfinder.civilization.direction.DirectionPlausibilityFilter;
import com.wayfinder.civilization.direction.DirectionReasonEvaluator;
import com.wayfinder.civilization.model.NodePurpose;
import com.wayfinder.civilization.relationship.DirectionalReferenceProposalFactory;
import com.wayfinder.civilization.relationship.RelationshipAdmissionService;
import com.wayfinder.civilization.relationship.RelationshipCommitService;
import com.wayfinder.civilization.relationship.RelationshipIdentityFactory;
import com.wayfinder.civilization.relationship.RelationshipProposalValidator;
import com.wayfinder.civilization.relationship.RelationshipTransitionFactory;
import com.wayfinder.civilization.relationship.RelationshipTransitionValidator;
import com.wayfinder.civilization.network.NetworkValidationContext;
import com.wayfinder.civilization.network.NodeAdmissionDecision;
import com.wayfinder.civilization.network.NodeAdmissionService;
import com.wayfinder.civilization.network.NodeProposalFactory;
import com.wayfinder.civilization.network.NodeProposalValidator;
import com.wayfinder.civilization.reason.ObservationReasonEvaluator;
import com.wayfinder.civilization.scoring.ObservationCandidateScorer;
import com.wayfinder.civilization.selection.DeterministicObservationSelector;
import com.wayfinder.civilization.selection.ObservationPlausibilityFilter;
import com.wayfinder.civilization.service.ObservationDecision;
import com.wayfinder.civilization.service.ObservationDecisionService;
import com.wayfinder.civilization.state.CivilizationState;
import com.wayfinder.config.WayfinderDevelopmentConfig;
import com.wayfinder.core.math.WorldBounds;
import com.wayfinder.geography.model.Landmark;
import com.wayfinder.geography.service.TerrainAnalysisReport;
import com.wayfinder.geography.service.TerrainAnalysisService;
import com.wayfinder.geography.visibility.HeightFieldVisibilityAnalyzer;
import com.wayfinder.geography.visibility.ObservationVisibilityEvaluator;
import com.wayfinder.minecraft.persistence.MinecraftSavedDataCivilizationRepository;
import com.wayfinder.minecraft.world.NeoForgeWorldTerrainView;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

import java.util.Locale;
import java.util.Optional;

/** Developer-facing Wayfinder instrumentation commands. */
public final class WayfinderCommands {
    private static final TerrainAnalysisService TERRAIN_ANALYSIS = new TerrainAnalysisService();


    private WayfinderCommands() {}

    public static void register(RegisterCommandsEvent event) {
        event.getDispatcher().register(
                Commands.literal("wayfinder")
                        .then(Commands.literal("analyze")
                                .executes(WayfinderCommands::analyze))
                        .then(Commands.literal("shrinepreview")
                                .executes(WayfinderShrinePreviewCommand::execute))
                        .then(Commands.literal("shrineplace")
                                .executes(WayfinderShrinePlaceCommand::execute))
                        .then(Commands.literal("towerpreview")
                                .executes(WayfinderWatchtowerPreviewCommand::execute))
                        .then(Commands.literal("towerplace")
                                .executes(WayfinderWatchtowerPlaceCommand::execute))
                        .then(Commands.literal("materializationstatus")
                                .executes(WayfinderMaterializationStatusCommand::execute))
                        .then(Commands.literal("routeloss")
                                .executes(WayfinderRouteLossCommand::execute))
                        .then(Commands.literal("breakcontinuation")
                                .executes(WayfinderBreakContinuationCommand::execute))
                        .then(Commands.literal("discoverystatus")
                                .executes(WayfinderDiscoveryStatusCommand::execute))
                        .then(Commands.literal("generatehistory")
                                .executes(HistoricalGenerationCommand::execute))
        );
    }

    private static int analyze(CommandContext<CommandSourceStack> context)
            throws CommandSyntaxException {

        CommandSourceStack source = context.getSource();
        ServerPlayer player = source.getPlayerOrException();
        ServerLevel level = source.getLevel();
        MinecraftSavedDataCivilizationRepository repository =
                new MinecraftSavedDataCivilizationRepository(level.getServer());
        CivilizationState civilizationState = repository.load();

        BlockPos center = player.blockPosition();

        int radius = WayfinderDevelopmentConfig.ANALYSIS_RADIUS;
        int spacing = WayfinderDevelopmentConfig.TERRAIN_SAMPLE_SPACING;

        WorldBounds bounds = new WorldBounds(
                center.getX() - radius,
                level.getMinY(),
                center.getZ() - radius,
                center.getX() + radius,
                level.getMaxY(),
                center.getZ() + radius
        );

        NeoForgeWorldTerrainView worldView = new NeoForgeWorldTerrainView(level);
        TerrainAnalysisReport report =
                TERRAIN_ANALYSIS.analyze(bounds, spacing, worldView);

        Landmark primary = report.primaryLandmark();

        Optional<ObservationDecision> observationDecision = Optional.empty();
        Optional<NodeAdmissionDecision> admissionDecision = Optional.empty();
        Optional<DirectionDecision> directionDecision = Optional.empty();
        boolean committed = false;
        boolean directionChainCommitted = false;
        String directionStage = "NOT_EVALUATED";

        if (primary != null) {
            ObservationVisibilityEvaluator visibilityEvaluator =
                    new ObservationVisibilityEvaluator(
                            new HeightFieldVisibilityAnalyzer(
                                    worldView,
                                    WayfinderDevelopmentConfig.VISIBILITY_RAY_SAMPLES
                            )
                    );

            ObservationDecisionService observationService =
                    new ObservationDecisionService(
                            new ObservationCandidateGenerator(visibilityEvaluator),
                            new ObservationCandidateValidator(0.75, 0.30),
                            new ObservationReasonEvaluator(),
                            new ObservationCandidateScorer(),
                            new ObservationPlausibilityFilter(0.55, 0.85),
                            new DeterministicObservationSelector()
                    );

            observationDecision =
                    observationService.decide(report.observationSites(), primary);

            if (observationDecision.isPresent()) {
                NodeAdmissionService admissionService =
                        new NodeAdmissionService(
                                new NodeProposalFactory(),
                                new NodeProposalValidator(
                                        0.55,
                                        12.0,
                                        48.0
                                )
                        );

                NetworkValidationContext networkContext =
                        new NetworkValidationContext(
                                civilizationState.nodes().stream()
                                        .map(node ->
                                                new com.wayfinder.civilization.network.CommittedNodeSnapshot(
                                                        node.id(),
                                                        node.purpose(),
                                                        node.position()
                                                )
                                        )
                                        .toList()
                        );

                NodeAdmissionDecision admission =
                        admissionService.evaluate(
                                observationDecision.get(),
                                networkContext
                        );

                admissionDecision = Optional.of(admission);

                if (admission.accepted()) {
                    NodeTransitionFactory transitionFactory =
                            new NodeTransitionFactory();

                    var transition = transitionFactory.create(admission);

                    if (transition.isPresent()) {
                        NodeCommitService commitService =
                                new NodeCommitService(
                                        new NodeTransitionValidator()
                                );

                        var commitResult =
                                commitService.commit(
                                        transition.get(),
                                        civilizationState
                                );

                        if (commitResult.committed()) {
                            civilizationState = commitResult.state();
                            repository.save(civilizationState);
                            committed = true;
                        }
                    }
                }
            }
        }


        /*
         * Once an Observation node is committed truth, it may justify a
         * Direction node. We deliberately target committed history rather
         * than a raw landmark.
         */
        if (primary != null) {
            var observationNode =
                    civilizationState.nodes()
                            .stream()
                            .filter(node ->
                                    node.purpose()
                                            == NodePurpose.OBSERVATION
                            )
                            .filter(node ->
                                    node.geographicTarget()
                                            .map(target ->
                                                    target.id()
                                                            .equals(
                                                                    primary.id()
                                                            )
                                            )
                                            .orElse(false)
                            )
                            .findFirst();

            if (observationNode.isPresent()) {
                DirectionDecisionService directionService =
                        new DirectionDecisionService(
                                new DirectionCandidateGenerator(
                                        worldView
                                ),
                                new DirectionCandidateValidator(
                                        0.35,
                                        40.0,
                                        160.0,
                                        16.0
                                ),
                                new DirectionReasonEvaluator(),
                                new DirectionCandidateScorer(),
                                new DirectionPlausibilityFilter(
                                        0.55,
                                        0.85
                                ),
                                new DeterministicDirectionSelector()
                        );

                directionDecision =
                        directionService.decide(
                                observationNode.get(),
                                civilizationState.nodes()
                        );

                if (directionDecision.isPresent()) {
                    DirectionChainCommitService chainService =
                            new DirectionChainCommitService(
                                    new NodeAdmissionService(
                                            new NodeProposalFactory(),
                                            new NodeProposalValidator(
                                                    0.55,
                                                    12.0,
                                                    48.0
                                            )
                                    ),
                                    new NodeTransitionFactory(),
                                    new NodeCommitService(
                                            new NodeTransitionValidator()
                                    ),
                                    new DirectionalReferenceProposalFactory(),
                                    new RelationshipAdmissionService(
                                            new RelationshipProposalValidator(
                                                    0.55
                                            )
                                    ),
                                    new RelationshipTransitionFactory(
                                            new RelationshipIdentityFactory()
                                    ),
                                    new RelationshipCommitService(
                                            new RelationshipTransitionValidator()
                                    )
                            );

                    var chainResult =
                            chainService.commit(
                                    directionDecision.get(),
                                    civilizationState
                            );

                    directionStage =
                            chainResult.stage();

                    if (chainResult.committed()) {
                        civilizationState =
                                chainResult.state();
                        repository.save(
                                civilizationState
                        );
                        directionChainCommitted =
                                true;
                    }
                } else {
                    directionStage =
                            "NO_DIRECTION_DECISION";
                }
            } else {
                directionStage =
                        "NO_MATCHING_OBSERVATION";
            }
        }

        String primaryText = primary == null
                ? "none"
                : String.format(
                        Locale.ROOT,
                        "%s %.2f",
                        primary.type(),
                        primary.salience()
                );

        String civilizationText =
                civilizationSummary(
                        observationDecision,
                        admissionDecision,
                        committed
                );

        int committedNodeCount =
                civilizationState.nodes().size();
        int committedRelationshipCount =
                civilizationState.relationships().size();
        String directionText =
                directionChainCommitted
                        ? "DIRECTION chain committed"
                        : "direction " + directionStage;

        source.sendSuccess(
                () -> Component.literal(String.format(
                        Locale.ROOT,
                        "Wayfinder: %s | high points %d | landmarks %d | observation sites %d | primary %s | %s | %s | nodes %d | relationships %d | %.2f ms",
                        report.profile(),
                        report.highPoints().size(),
                        report.landmarks().size(),
                        report.observationSites().size(),
                        primaryText,
                        civilizationText,
                        directionText,
                        committedNodeCount,
                        committedRelationshipCount,
                        report.elapsedMillis()
                )),
                false
        );

        WayfinderMod.LOGGER.info(
                "Wayfinder semantic analysis at [{}, {}]: profile={}, samples={}, highPoints={}, landmarks={}, observationSites={}, committedNodes={}, elapsedMs={}",
                center.getX(),
                center.getZ(),
                report.profile(),
                report.sampleCount(),
                report.highPoints().size(),
                report.landmarks().size(),
                report.observationSites().size(),
                civilizationState.nodes().size(),
                String.format(Locale.ROOT, "%.2f", report.elapsedMillis())
        );

        if (observationDecision.isPresent()) {
            logObservationDecision(observationDecision.get());
        }

        if (admissionDecision.isPresent()) {
            logAdmission(admissionDecision.get(), committed, civilizationState);
        }

        return Command.SINGLE_SUCCESS;
    }

    private static String civilizationSummary(
            Optional<ObservationDecision> observationDecision,
            Optional<NodeAdmissionDecision> admissionDecision,
            boolean committed
    ) {
        if (observationDecision.isEmpty()) {
            return "no civilization decision";
        }

        if (admissionDecision.isEmpty()) {
            return "decision made | no admission result";
        }

        if (!admissionDecision.get().accepted()) {
            String issue = admissionDecision.get()
                    .validation()
                    .issues()
                    .stream()
                    .findFirst()
                    .map(item -> item.code().name())
                    .orElse("UNKNOWN");

            return "proposal rejected " + issue;
        }

        return committed
                ? "OBSERVATION committed"
                : "proposal admitted | not committed";
    }

    private static void logObservationDecision(
            ObservationDecision decision
    ) {
        var selected = decision.selected();
        var candidate = selected.candidate();

        WayfinderMod.LOGGER.info(
                "Wayfinder observation decision: observer=[{}, {}, {}], target=[{}, {}, {}], score={}, generated={}, valid={}, plausible={}",
                candidate.position().x(),
                candidate.position().y(),
                candidate.position().z(),
                candidate.target().anchor().x(),
                candidate.target().anchor().y(),
                candidate.target().anchor().z(),
                String.format(
                        Locale.ROOT,
                        "%.3f",
                        selected.score().finalScore()
                ),
                decision.generatedCandidates(),
                decision.validCandidates(),
                decision.plausibleCandidates()
        );
    }

    private static void logAdmission(
            NodeAdmissionDecision admission,
            boolean committed,
            CivilizationState civilizationState
    ) {
        WayfinderMod.LOGGER.info(
                "Wayfinder node admission: proposalKey={}, accepted={}, committed={}, issues={}",
                admission.proposal().proposalKey(),
                admission.accepted(),
                committed,
                admission.validation().issues()
        );

        if (committed) {
            civilizationState.nodes()
                    .stream()
                    .filter(node ->
                            node.sourceProposalKey()
                                    .equals(
                                            admission.proposal().proposalKey()
                                    )
                    )
                    .findFirst()
                    .ifPresent(node ->
                            WayfinderMod.LOGGER.info(
                                    "Wayfinder committed node: id={}, purpose={}, pos=[{}, {}, {}], target=[{}, {}, {}], score={}",
                                    node.id().value(),
                                    node.purpose(),
                                    node.position().x(),
                                    node.position().y(),
                                    node.position().z(),
                                    node.geographicTarget().map(target -> target.anchor().x()).orElse(node.position().x()),
                                    node.geographicTarget().map(target -> target.anchor().y()).orElse(node.position().y()),
                                    node.geographicTarget().map(target -> target.anchor().z()).orElse(node.position().z()),
                                    String.format(
                                            Locale.ROOT,
                                            "%.3f",
                                            node.score()
                                    )
                            )
                    );
        }
    }
}

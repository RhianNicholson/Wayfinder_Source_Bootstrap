package com.wayfinder.minecraft.history;

import com.wayfinder.core.random.DefaultRandomStreamFactory;
import com.wayfinder.core.random.Sha256SeedDeriver;
import com.wayfinder.history.*;
import com.wayfinder.minecraft.persistence.MinecraftSavedDataCivilizationRepository;
import com.wayfinder.minecraft.persistence.MinecraftSavedDataHistoricalEventRepository;
import com.wayfinder.minecraft.persistence.MinecraftSavedDataMaterializationRepository;
import com.wayfinder.minecraft.structure.MinecraftMaterializationBlockView;
import com.wayfinder.structure.materialization.MaterializationCommitService;
import com.wayfinder.structure.materialization.MaterializationInspector;
import net.minecraft.server.level.ServerLevel;

public final class MinecraftHistoricalGenerationOrchestrator {

    public Result run(ServerLevel level, HistoricalScope scope) {
        var server = level.getServer();
        var civilizationRepository = new MinecraftSavedDataCivilizationRepository(server);
        var materializationRepository = new MinecraftSavedDataMaterializationRepository(server);
        var historyRepository = new MinecraftSavedDataHistoricalEventRepository(server);

        var civilization = civilizationRepository.load();
        var materialization = materializationRepository.load();
        var history = historyRepository.load();

        var generator = new HistoricalGenerationService(
                new ProceduralRouteLossSelector(),
                new DeterministicRouteLossDecisionService(
                        new DefaultRandomStreamFactory(new Sha256SeedDeriver())),
                new HistoricalEventFactory(),
                new HistoricalEventCommitService(),
                new RouteLossTransitionService());

        var generated = generator.generate(
                level.getSeed(),
                scope,
                civilization,
                materialization,
                history);

        if (generated.committedEvent().isPresent()) {
            historyRepository.save(generated.state());
            history = generated.state();
        }

        var work = new HistoricalPhysicalizationPlanner().plan(history, materialization);

        if (!work.requiresPhysicalization() || work.targetNodeId().isEmpty()) {
            return new Result(
                    generated.committedEvent().isPresent(),
                    false, 0,
                    generated.decision().roll(),
                    generated.decision().threshold());
        }

        var nodeId = work.targetNodeId().orElseThrow();
        var record = materialization.find(nodeId).orElseThrow();

        /*
         * The domain planner identifies a historically relevant target. Exact
         * Minecraft block truth decides whether the consequence is still
         * physically pending. This avoids using DAMAGED/LOST as a proxy.
         */
        var physicalTruth = new MinecraftRouteLossPhysicalizationInspector();
        if (!physicalTruth.requiresPhysicalization(level, record)) {
            return new Result(
                    generated.committedEvent().isPresent(),
                    false, 0,
                    generated.decision().roll(),
                    generated.decision().threshold());
        }

        int removed = new MinecraftRouteLossApplier()
                .removeRecordedStructure(level, record);

        var inspection = new MaterializationInspector().inspect(
                record, new MinecraftMaterializationBlockView(level));

        var updated = new MaterializationCommitService()
                .updateCondition(materialization, record, inspection.condition());

        materializationRepository.save(updated.state());

        return new Result(
                generated.committedEvent().isPresent(),
                true, removed,
                generated.decision().roll(),
                generated.decision().threshold());
    }

    @Deprecated
    public Result run(
            ServerLevel level,
            String regionKey,
            String eraKey,
            int generationVersion
    ) {
        throw new UnsupportedOperationException(
                "String historical scope is retired. Use run(ServerLevel, HistoricalScope).");
    }

    public record Result(
            boolean eventCommitted,
            boolean physicalized,
            int removedBlocks,
            double roll,
            double threshold
    ) {}
}

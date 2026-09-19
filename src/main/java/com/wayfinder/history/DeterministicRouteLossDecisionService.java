package com.wayfinder.history;

import com.wayfinder.core.random.RandomStreamFactory;
import com.wayfinder.core.random.RandomStreamKey;

import java.util.List;
import java.util.Objects;

/**
 * Chooses among already-plausible route-loss candidates with a scoped,
 * deterministic random stream.
 *
 * Randomness never creates eligibility.
 */
public final class DeterministicRouteLossDecisionService {
    public static final String STAGE = "historical-route-loss";

    private final RandomStreamFactory randomStreams;

    public DeterministicRouteLossDecisionService(RandomStreamFactory randomStreams) {
        this.randomStreams = Objects.requireNonNull(randomStreams, "randomStreams");
    }

    public RouteLossDecision decide(
            long worldSeed,
            String regionKey,
            String eraKey,
            int generationVersion,
            List<RouteLossCandidate> candidates
    ) {
        if (candidates.isEmpty()) {
            return RouteLossDecision.none();
        }

        var random = randomStreams.create(
                new RandomStreamKey(
                        worldSeed,
                        regionKey,
                        STAGE,
                        eraKey,
                        generationVersion
                )
        );

        /*
         * Weighted choice is made only among candidates already admitted by
         * ProceduralRouteLossSelector.
         */
        double totalWeight = candidates.stream()
                .mapToDouble(RouteLossCandidate::vulnerability)
                .sum();

        double pick = random.nextDouble() * totalWeight;
        RouteLossCandidate selected = candidates.get(candidates.size() - 1);

        double cursor = 0.0;
        for (var candidate : candidates) {
            cursor += candidate.vulnerability();
            if (pick < cursor) {
                selected = candidate;
                break;
            }
        }

        /*
         * The selected candidate's vulnerability is also its occurrence
         * threshold. A second draw decides whether this era actually lost it.
         */
        double roll = random.nextDouble();
        boolean occurs = roll < selected.vulnerability();

        return new RouteLossDecision(
                occurs,
                java.util.Optional.of(selected),
                roll,
                selected.vulnerability()
        );
    }
}

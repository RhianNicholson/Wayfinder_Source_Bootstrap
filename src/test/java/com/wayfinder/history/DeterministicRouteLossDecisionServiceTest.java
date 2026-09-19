package com.wayfinder.history;

import com.wayfinder.core.id.NodeId;
import com.wayfinder.core.random.DefaultRandomStreamFactory;
import com.wayfinder.core.random.Sha256SeedDeriver;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

final class DeterministicRouteLossDecisionServiceTest {
    private final DeterministicRouteLossDecisionService service =
            new DeterministicRouteLossDecisionService(
                    new DefaultRandomStreamFactory(new Sha256SeedDeriver()));

    @Test
    void sameScopeProducesSameHistoricalDecision() {
        var candidates = candidates();
        var first = service.decide(12345L, "region:0,0", "era:2", 1, candidates);
        var second = service.decide(12345L, "region:0,0", "era:2", 1, candidates);
        assertEquals(first, second);
    }

    @Test
    void noCandidateMeansRandomnessCannotCreateHistory() {
        var decision = service.decide(12345L, "region:0,0", "era:2", 1, List.of());
        assertFalse(decision.occurs());
        assertTrue(decision.candidate().isEmpty());
    }

    private static List<RouteLossCandidate> candidates() {
        return List.of(
                new RouteLossCandidate(
                        id("11111111-1111-1111-1111-111111111111"),
                        id("22222222-2222-2222-2222-222222222222"),
                        0.45, "eligible"),
                new RouteLossCandidate(
                        id("33333333-3333-3333-3333-333333333333"),
                        id("44444444-4444-4444-4444-444444444444"),
                        0.70, "eligible")
        );
    }

    private static NodeId id(String value) {
        return new NodeId(UUID.fromString(value));
    }
}

package com.wayfinder.civilization.selection;

import com.wayfinder.civilization.candidate.ObservationCandidate;
import com.wayfinder.civilization.reason.ReasonEvaluation;
import com.wayfinder.civilization.reason.ReasonRecord;
import com.wayfinder.civilization.reason.ReasonType;
import com.wayfinder.civilization.scoring.ObservationCandidateScore;
import com.wayfinder.civilization.scoring.ScoredObservationCandidate;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

final class ObservationPlausibilityFilterTest {

    @Test
    void removesCandidatesOutsideRelativeBand() {
        var filter = new ObservationPlausibilityFilter(0.55, 0.85);

        var candidates = List.of(
                scored("best", 0.90),
                scored("near", 0.80),
                scored("weak", 0.60)
        );

        var eligible = filter.eligible(candidates);

        assertEquals(2, eligible.size());
        assertEquals("best", eligible.get(0).candidate().candidateKey());
        assertEquals("near", eligible.get(1).candidate().candidateKey());
    }

    private static ScoredObservationCandidate scored(String key, double finalScore) {
        var candidate = new ObservationCandidate(key, null, null, null, null);
        var reasons = new ReasonEvaluation(
                new ReasonRecord(ReasonType.OBSERVATION, 1.0, "test"),
                List.of()
        );
        var score = new ObservationCandidateScore(1, 1, 1, 1, finalScore);
        return new ScoredObservationCandidate(candidate, reasons, score);
    }
}

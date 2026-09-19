package com.wayfinder.civilization.candidate;

import com.wayfinder.geography.model.Landmark;
import com.wayfinder.geography.model.ObservationSite;
import com.wayfinder.geography.visibility.ObservationVisibility;
import com.wayfinder.geography.visibility.ObservationVisibilityEvaluator;

import java.util.List;

public final class ObservationCandidateGenerator {
    private final ObservationVisibilityEvaluator visibilityEvaluator;

    public ObservationCandidateGenerator(ObservationVisibilityEvaluator visibilityEvaluator) {
        this.visibilityEvaluator = visibilityEvaluator;
    }

    public List<ObservationCandidate> generate(List<ObservationSite> sites, Landmark target) {
        return visibilityEvaluator.evaluate(sites, target).stream()
                .map(this::toCandidate)
                .toList();
    }

    private ObservationCandidate toCandidate(ObservationVisibility visibility) {
        ObservationSite site = visibility.site();
        String key = site.position().x() + ":" + site.position().y() + ":" + site.position().z()
                + "->" + visibility.target().id().value();

        return new ObservationCandidate(
                key,
                site.position(),
                visibility.target(),
                site,
                visibility.sightline()
        );
    }
}

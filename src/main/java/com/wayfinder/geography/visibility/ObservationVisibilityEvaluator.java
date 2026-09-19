package com.wayfinder.geography.visibility;

import com.wayfinder.geography.model.Landmark;
import com.wayfinder.geography.model.ObservationSite;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public final class ObservationVisibilityEvaluator {
    private final VisibilityAnalyzer analyzer;

    public ObservationVisibilityEvaluator(VisibilityAnalyzer analyzer) {
        this.analyzer = analyzer;
    }

    public List<ObservationVisibility> evaluate(
            List<ObservationSite> sites,
            Landmark target
    ) {
        return sites.stream()
                .filter(site -> site.target().id().equals(target.id()))
                .map(site -> {
                    SightlineResult result = analyzer.analyze(site.position(), target);
                    double recognizability =
                            (result.score() * 0.65)
                            + (target.salience() * 0.25)
                            + (site.buildability() * 0.10);
                    return new ObservationVisibility(site, target, result, clamp(recognizability));
                })
                .sorted(Comparator.comparingDouble(ObservationVisibility::recognizability).reversed())
                .toList();
    }

    public Optional<ObservationVisibility> bestVisible(
            List<ObservationSite> sites,
            Landmark target
    ) {
        return evaluate(sites, target).stream()
                .filter(v -> v.sightline().visible())
                .findFirst();
    }

    private static double clamp(double value) {
        return Math.max(0.0, Math.min(1.0, value));
    }
}

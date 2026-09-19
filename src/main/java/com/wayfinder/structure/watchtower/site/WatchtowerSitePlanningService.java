package com.wayfinder.structure.watchtower.site;

import com.wayfinder.geography.world.WorldTerrainView;
import com.wayfinder.structure.watchtower.WatchtowerGeometryPlan;

import java.util.Optional;

public final class WatchtowerSitePlanningService {
    private final WatchtowerSiteValidator validator;
    private final WatchtowerTerrainAdapter adapter;

    public WatchtowerSitePlanningService(
            WatchtowerSiteValidator validator,
            WatchtowerTerrainAdapter adapter
    ) {
        this.validator = validator;
        this.adapter = adapter;
    }

    public Result evaluate(
            WatchtowerGeometryPlan tower,
            WorldTerrainView world
    ) {
        var validation = validator.validate(tower, world);

        if (!validation.accepted()) {
            return new Result(validation, Optional.empty());
        }

        return new Result(
                validation,
                Optional.of(adapter.adapt(tower, validation, world))
        );
    }

    public record Result(
            WatchtowerSiteValidationResult validation,
            Optional<AdaptedWatchtowerGeometry> adapted
    ) {
        public Result {
            adapted = adapted == null ? Optional.empty() : adapted;
            if (validation.accepted() != adapted.isPresent()) {
                throw new IllegalArgumentException(
                        "accepted site must have adapted geometry; rejected site must not"
                );
            }
        }
    }
}

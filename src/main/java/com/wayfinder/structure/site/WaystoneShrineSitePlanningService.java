package com.wayfinder.structure.site;

import com.wayfinder.geography.world.WorldTerrainView;
import com.wayfinder.structure.geometry.StructureGeometryPlan;

import java.util.Optional;

/**
 * Validation gate between abstract Shrine geometry and any future Minecraft
 * renderer.
 */
public final class WaystoneShrineSitePlanningService {
    private final WaystoneShrineSiteValidator validator;
    private final WaystoneShrineTerrainAdapter adapter;

    public WaystoneShrineSitePlanningService(
            WaystoneShrineSiteValidator validator,
            WaystoneShrineTerrainAdapter adapter
    ) {
        this.validator = validator;
        this.adapter = adapter;
    }

    public Result evaluate(
            StructureGeometryPlan geometry,
            WorldTerrainView world
    ) {
        ShrineSiteValidationResult validation =
                validator.validate(
                        geometry,
                        world
                );

        if (!validation.accepted()) {
            return new Result(
                    validation,
                    Optional.empty()
            );
        }

        return new Result(
                validation,
                Optional.of(
                        adapter.adapt(
                                geometry,
                                validation,
                                world
                        )
                )
        );
    }

    public record Result(
            ShrineSiteValidationResult validation,
            Optional<AdaptedShrineGeometry> adapted
    ) {
        public Result {
            if (validation == null) {
                throw new IllegalArgumentException(
                        "validation is required"
                );
            }

            adapted = adapted == null
                    ? Optional.empty()
                    : adapted;

            if (validation.accepted()
                    != adapted.isPresent()) {
                throw new IllegalArgumentException(
                        "Accepted site must have adapted geometry; rejected site must not."
                );
            }
        }
    }
}

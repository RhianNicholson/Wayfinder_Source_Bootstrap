package com.wayfinder.structure.shrine;

import com.wayfinder.structure.geometry.BlockFunction;
import com.wayfinder.structure.geometry.StructureGeometryPlan;

import java.util.EnumMap;
import java.util.Map;

/**
 * Compact developer-facing view of a solved Shrine.
 */
public record WaystoneShrineDryRunSummary(
        int totalBlocks,
        Map<BlockFunction, Integer> blocksByFunction
) {
    public WaystoneShrineDryRunSummary {
        blocksByFunction = Map.copyOf(blocksByFunction);
    }

    public static WaystoneShrineDryRunSummary from(
            StructureGeometryPlan plan
    ) {
        EnumMap<BlockFunction, Integer> counts =
                new EnumMap<>(BlockFunction.class);

        for (var block : plan.blocks()) {
            counts.merge(
                    block.function(),
                    1,
                    Integer::sum
            );
        }

        return new WaystoneShrineDryRunSummary(
                plan.blocks().size(),
                counts
        );
    }
}

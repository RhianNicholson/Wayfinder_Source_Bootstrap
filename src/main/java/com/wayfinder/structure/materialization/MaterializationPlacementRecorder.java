package com.wayfinder.structure.materialization;

import com.wayfinder.structure.geometry.StructureGeometryPlan;

public final class MaterializationPlacementRecorder {
    private final MaterializationRecordFactory factory;
    private final MaterializationCommitService commitService;

    public MaterializationPlacementRecorder(
            MaterializationRecordFactory factory,
            MaterializationCommitService commitService
    ) {
        this.factory = factory;
        this.commitService = commitService;
    }

    public MaterializationCommitService.Result record(
            MaterializationState current,
            StructureGeometryPlan placedGeometry
    ) {
        return commitService.recordPlacement(
                current,
                factory.fromPlacedGeometry(placedGeometry)
        );
    }
}

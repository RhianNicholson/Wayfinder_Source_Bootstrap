package com.wayfinder.structure.watchtower.site;

import com.wayfinder.geography.world.WorldTerrainView;
import com.wayfinder.structure.geometry.BlockFunction;
import com.wayfinder.structure.watchtower.WatchtowerGeometryPlan;
import com.wayfinder.structure.watchtower.WatchtowerSightlineSolver;

import java.util.ArrayList;
import java.util.List;

/**
 * Real-site gate for the Watchtower.
 *
 * The already-solved platform height is historical architecture. Validation
 * may reject it; it may not silently choose a different tower height.
 */
public final class WatchtowerSiteValidator {
    private final int maximumSupportExtension;
    private final WatchtowerSightlineSolver sightlineVerifier;

    public WatchtowerSiteValidator(
            int maximumSupportExtension,
            WatchtowerSightlineSolver sightlineVerifier
    ) {
        this.maximumSupportExtension = maximumSupportExtension;
        this.sightlineVerifier = sightlineVerifier;
    }

    public WatchtowerSiteValidationResult validate(
            WatchtowerGeometryPlan tower,
            WorldTerrainView world
    ) {
        List<WatchtowerSiteIssue> issues = new ArrayList<>();
        int deepestExtension = 0;

        for (var cell : tower.geometry().blocks()) {
            if (cell.function() == BlockFunction.TOWER_FOUNDATION) {
                int surfaceY = world.surfaceHeight(
                        cell.position().x(),
                        cell.position().z()
                ) - 1;

                int drop = cell.position().y() - surfaceY;
                deepestExtension = Math.max(deepestExtension, drop);

                if (drop > maximumSupportExtension) {
                    issues.add(new WatchtowerSiteIssue(
                            WatchtowerSiteIssueCode.SUPPORT_DROP_TOO_DEEP,
                            "A tower support would require an implausibly deep extension."
                    ));
                }

                if (world.isWater(
                        cell.position().x(),
                        surfaceY,
                        cell.position().z()
                )) {
                    issues.add(new WatchtowerSiteIssue(
                            WatchtowerSiteIssueCode.WATER_AT_SUPPORT,
                            "A tower support terminates in water."
                    ));
                }
            }

            if ((cell.function() == BlockFunction.OBSERVATION_PLATFORM
                    || cell.function() == BlockFunction.VIEW_FRAME)
                    && world.isSolid(
                            cell.position().x(),
                            cell.position().y(),
                            cell.position().z()
                    )) {
                issues.add(new WatchtowerSiteIssue(
                        WatchtowerSiteIssueCode.PLATFORM_SPACE_BLOCKED,
                        "Natural terrain occupies required observation-platform space."
                ));
            }
        }

        var intent = tower.geometry().materializationPlan().intent();
        var verified = sightlineVerifier.solve(
                intent.origin(),
                intent.semanticTarget(),
                world
        );

        if (!verified.solved()
                || verified.towerHeight() > tower.sightline().towerHeight()) {
            issues.add(new WatchtowerSiteIssue(
                    WatchtowerSiteIssueCode.SIGHTLINE_LOST,
                    "The committed platform height no longer provides the required landmark sightline."
            ));
        }

        /*
         * Protect the immediate target-facing view arc above the platform.
         * This is separate from the long-range terrain sightline.
         */
        int fx = intent.facing().stepX();
        int fz = intent.facing().stepZ();
        int platformY = tower.sightline().platformY();

        for (int distance = 1; distance <= 3; distance++) {
            int x = intent.origin().x() + (fx * distance);
            int z = intent.origin().z() + (fz * distance);

            for (int y = platformY + 1; y <= platformY + 2; y++) {
                if (world.isSolid(x, y, z)) {
                    issues.add(new WatchtowerSiteIssue(
                            WatchtowerSiteIssueCode.VIEW_ARC_BLOCKED,
                            "The target-facing observation arc is physically obstructed."
                    ));
                    distance = 4;
                    break;
                }
            }
        }

        return new WatchtowerSiteValidationResult(
                issues.isEmpty(),
                deepestExtension,
                issues
        );
    }
}

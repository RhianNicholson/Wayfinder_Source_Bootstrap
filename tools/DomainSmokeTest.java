import com.wayfinder.core.math.Bearing;
import com.wayfinder.core.math.WorldBounds;
import com.wayfinder.core.random.RandomStreamKey;
import com.wayfinder.core.random.Sha256SeedDeriver;
import com.wayfinder.geography.analysis.DefaultTerrainSampler;
import com.wayfinder.geography.analysis.TerrainMetricsCalculator;
import com.wayfinder.geography.analysis.TerrainProfileClassifier;
import com.wayfinder.geography.feature.HighPointDetector;
import com.wayfinder.geography.feature.LandmarkDetector;
import com.wayfinder.geography.feature.ObservationSiteDetector;
import com.wayfinder.geography.model.TerrainProfile;
import com.wayfinder.testsupport.SyntheticTerrainView;

public final class DomainSmokeTest {
    public static void main(String[] args) {
        requireClose(new Bearing(-10).degrees(), 350.0, "Bearing normalization");
        requireClose(new Bearing(355).angularDistance(new Bearing(5)), 10.0, "Bearing wrap distance");

        var deriver = new Sha256SeedDeriver();
        var key = new RandomStreamKey(1234L, "region-a", "candidate", "observation", 1);
        require(deriver.derive(key) == deriver.derive(key), "Seed derivation must be deterministic");
        require(deriver.derive(key) != deriver.derive(
            new RandomStreamKey(1234L, "region-a", "candidate", "direction", 1)),
            "Random scopes must derive different seeds");

        var flat = new SyntheticTerrainView((x, z) -> 64);
        var bounds = new WorldBounds(-128, -64, -128, 128, 320, 128);
        var sampler = new DefaultTerrainSampler();
        var metricsCalculator = new TerrainMetricsCalculator();
        var grid = sampler.sample(bounds, 8, flat);
        var metrics = metricsCalculator.calculate(grid);
        var profile = new TerrainProfileClassifier().classify(metrics);

        require(grid.samples().size() == 1089, "256-ish square sampled every 8 blocks should produce 1089 samples");
        requireClose(metrics.meanElevation(), 64.0, "Flat terrain mean elevation");
        require(profile == TerrainProfile.FLAT, "Flat synthetic terrain must classify FLAT");
        require(new HighPointDetector().detect(grid).isEmpty(), "Flat terrain must not create high points");

        var twinHills = new SyntheticTerrainView((x, z) -> {
            double d1 = Math.sqrt((x + 56.0) * (x + 56.0) + z * (double) z);
            double d2 = Math.sqrt((x - 56.0) * (x - 56.0) + (z - 24.0) * (z - 24.0));
            int h1 = Math.max(0, 52 - (int) d1);
            int h2 = Math.max(0, 38 - (int) d2);
            return 64 + Math.max(h1, h2);
        });
        var hillGrid = sampler.sample(bounds, 8, twinHills);
        var hillMetrics = metricsCalculator.calculate(hillGrid);
        var highPoints = new HighPointDetector().detect(hillGrid);
        var landmarks = new LandmarkDetector().detect(highPoints, hillMetrics);
        var observationSites = new ObservationSiteDetector().detect(highPoints, landmarks);

        require(!highPoints.isEmpty(), "Twin hill terrain must yield high points");
        require(!landmarks.isEmpty(), "Twin hill terrain must yield landmarks");
        require(!observationSites.isEmpty(), "Twin hill terrain must yield an observation opportunity");

        System.out.println("Wayfinder domain smoke test PASS");
        System.out.println("samples=" + grid.samples().size());
        System.out.println("profile=" + profile);
        System.out.println("semanticHighPoints=" + highPoints.size());
        System.out.println("semanticLandmarks=" + landmarks.size());
        System.out.println("semanticObservationSites=" + observationSites.size());
        System.out.println("stableSeed=" + deriver.derive(key));
    }

    private static void require(boolean condition, String message) {
        if (!condition) throw new AssertionError(message);
    }

    private static void requireClose(double actual, double expected, String message) {
        if (Math.abs(actual - expected) > 1e-9) {
            throw new AssertionError(message + ": expected " + expected + ", got " + actual);
        }
    }
}

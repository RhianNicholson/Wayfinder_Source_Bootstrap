# Milestone 19.2 — Watchtower Ridge Test Fixture Fix

The Milestone 19 sightline solver compiled correctly, but the ridge fixture in
`interveningRidgeForcesHigherPlatform` was too low to obstruct the minimum
6-block platform.

With the landmark at Y=82 and the observer eye height added to a 6-block tower,
a ridge surface height of 77 still leaves the sampled ray fully clear.

The fixture now uses a ridge height of 80.

Under the existing solver:

- 6-block platform visibility score: about 0.62 -> rejected
- 7-block platform visibility score: about 0.73 -> rejected
- 8-block platform visibility score: 1.00 -> accepted

So the test now measures the intended behavior rather than assuming an
obstruction that the geometry does not actually create.

No production code or thresholds changed.

Apply after Milestone 19.1 and run:

```powershell
.\gradlew.bat clean test
.\gradlew.bat build
```

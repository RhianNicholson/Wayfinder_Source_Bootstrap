# Wayfinder Milestone 3 — Visibility Validation

This overlay adds the first sightline system to the loader-neutral geography engine.

## Added
- `SightlineResult`
- `VisibilityAnalyzer`
- `HeightFieldVisibilityAnalyzer`
- `ObservationVisibility`
- `ObservationVisibilityEvaluator`
- synthetic tests for clear and blocked sightlines

## Intent
This milestone turns an observation *candidate* into an observation site that can be evaluated against a meaningful landmark.

The analyzer deliberately uses the `WorldTerrainView` abstraction, so the civilization/geography engine remains independent of Minecraft classes.

## Next integration step
Wire `ObservationVisibilityEvaluator` into the existing `/wayfinder analyze` command after the primary landmark and observation sites have been selected. Report:
- number of sites with a valid sightline
- best sightline score
- best recognizability score
- observer and target positions in developer logs

This is the final geography gate before creating the first logical `OBSERVATION` Node.

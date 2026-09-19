# Wayfinder Milestone 5.1 — Compile Fix

Fixes the Milestone 5 compile error in `ObservationCandidateScorer`.

## Cause

Milestone 5 referenced:

`candidate.site().terrainFit()`

but the existing `ObservationSite` model has no `terrainFit()` accessor.

## Fix

The scorer now uses:

`candidate.site().score()`

as the terrain/geographic-fit input.

This is preferable to adding a redundant field because `ObservationSite.score()` is already the semantic-geography layer's combined coarse assessment of the site's suitability.

The civilization layer therefore consumes an existing lower-layer fact rather than reaching back into terrain analysis or changing the geography model merely to satisfy the scorer.

## Apply

Overlay this patch after Milestone 5, then run:

`.\gradlew.bat build`

If successful:

`.\gradlew.bat runClient`

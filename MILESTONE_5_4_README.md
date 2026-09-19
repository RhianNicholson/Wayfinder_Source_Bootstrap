# Wayfinder Milestone 5.4 — Sightline Blocking Fix

The failing `interveningRidgeBlocksSightline()` test exposed a real weakness in
the first coarse visibility algorithm.

## Problem

The old analyzer used:

`score = clear samples / total samples`

with visibility accepted at `score >= 0.75`.

A broad ridge could block roughly 20–25% of the ray while still leaving enough
samples clear for the analyzer to incorrectly classify the landmark as visible.

## Fix

The analyzer now distinguishes:

- scattered blocked samples
- continuous blocked terrain

It tracks the longest continuous blocked run and applies an additional
obstruction penalty for sustained blockage.

This means:

- a real intervening ridge should fail
- a single noisy height-field sample should not automatically fail the sightline

## Tests

The patch now tests:

1. clear terrain is visible
2. a broad intervening ridge blocks visibility
3. one isolated obstruction sample does not automatically invalidate the ray

## Run

```powershell
.\gradlew.bat clean test
.\gradlew.bat build
```

If both pass, run:

```powershell
.\gradlew.bat runClient
```

# Milestone 41 — Region-Bounded Historical Candidates

M40 gave each live historical pass a regional deterministic RNG scope. M41
closes the more important causality boundary: the candidates themselves are now
regional.

For a route-loss continuation to participate in a regional history pass:

```text
DIRECTION node region == selected HistoricalRegionKey
AND
OBSERVATION node region == selected HistoricalRegionKey
```

Only then may its materialized destination become a candidate.

This prevents:

```text
Region A historical pass
        ↓
Region B Watchtower destroyed
```

even if Region B contains a globally valid continuation.

`HistoricalGenerationService` now has a scope-native overload accepting
`HistoricalScope`. The older string-key overload remains temporarily for
compatibility while the Minecraft orchestrator is migrated.

## Cross-region relationships

For this first rule, a continuation crossing a historical-region boundary is
not eligible for route loss from either region. That is intentional: ownership
of cross-region infrastructure needs an explicit civilization rule rather than
being inferred from coordinates.

## Test

Overlay after M40:

```powershell
.\gradlew.bat clean test
.\gradlew.bat build
```

No runClient test is required for this domain boundary.

## Next

M42 will migrate the Minecraft orchestrator to accept `HistoricalScope`
directly, eliminating the compatibility string path. After that, region-aware
history will be end-to-end rather than only available in the domain service.

# Milestone 52.1 — HistoricalRegionKey API Fix

The M52 implementation incorrectly assumed `HistoricalRegionKey` exposed a
`value()` accessor.

This patch removes that assumption entirely. `CivilizationEraState` now uses
`HistoricalRegionKey` itself as the map key:

```text
HistoricalRegionKey -> CivilizationEra
```

That is also a stronger domain model: era state is keyed by the semantic region
identity rather than by a lossy/string representation of it.

`HashMap` is used while producing the updated immutable state so no ordering or
`Comparable` requirement is imposed on `HistoricalRegionKey`.

Apply over M52 and run:

```powershell
.\gradlew.bat clean test
.\gradlew.bat build
```

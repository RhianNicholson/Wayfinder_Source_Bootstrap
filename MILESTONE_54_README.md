# Milestone 54 — Live Persisted Era Scope

M53 validated persistent regional civilization-era state.

M54 introduces the live scope-resolution boundary:

```text
WorldPosition
     ↓
HistoricalRegionKey.from(position)
     ↓
MinecraftSavedDataCivilizationEraRepository
     ↓
CivilizationEraState.eraFor(region)
     ↓
HistoricalScope(region, era.key(), generationVersion)
```

New class:

- `MinecraftHistoricalScopeResolver`

This removes the need for live callers to know how era state is stored or how
semantic region identity is derived.

## Important integration note

This milestone deliberately adds the resolver as a compile-validated boundary
without replacing `WayfinderCommands` yet. The exact current command file has
changed through many prior milestones and was not included in the persistence
upload used for M53/M54.

After this patch compiles, the next patch can update the exact current command
source (if supplied) so `/wayfinder generatehistory` uses this resolver and an
explicit developer era-advance command can be registered without reconstructing
the command tree from an older snapshot.

## Validation

Apply after M53:

```powershell
.\gradlew.bat clean test
.\gradlew.bat build
```

No runClient test is required for this boundary-only milestone.

## Next

M54.1/live wiring:
- resolve `/wayfinder generatehistory` scope from persisted regional era state;
- add an explicit developer era-advance command;
- persist the transition through the M53 repository;
- report region and before/after era for diagnostics.

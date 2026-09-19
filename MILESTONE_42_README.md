# Milestone 42 — End-to-End Historical Scope

M41 made candidate discovery region-aware. M42 removes the remaining live
string-key handoff.

The Minecraft historical orchestrator now accepts:

```java
run(ServerLevel level, HistoricalScope scope)
```

and passes that scope directly to `HistoricalGenerationService`.

The live path is now:

```text
command source position
        ↓
HistoricalScope
        ↓
MinecraftHistoricalGenerationOrchestrator
        ↓
HistoricalGenerationService
        ↓
region-bounded candidate discovery
        ↓
region + era deterministic RNG
        ↓
validated event commit
        ↓
physical consequence
```

The old string overload remains only as a fail-fast deprecated compile bridge.
It no longer performs historical generation. This prevents new code from
silently bypassing region-bounded candidate selection.

## Test

Overlay after M41:

```powershell
.\gradlew.bat clean test
.\gradlew.bat build
.\gradlew.bat runClient
```

Then run:

```text
/wayfinder generatehistory
```

Expected:
- command is registered,
- region and era are printed,
- no exception from the retired string path,
- generation behavior remains deterministic,
- only candidates inside the reported region are eligible.

## Important next issue

M32 deliberately leaves authentic Watchtower remnants. Therefore a completed
ROUTE_LOSS can leave its materialization condition as DAMAGED rather than LOST.

The current recovery planner predates that rule and treats any non-LOST record
as pending physicalization. That can cause a completed ruin consequence to be
requested repeatedly, even though the applier will preserve the approved
remnants.

M43 should fix physicalization completion using exact block truth: a route-loss
consequence is pending only while at least one original non-remnant cell that
should have been removed still exists with its expected material.

That preserves both rules:

- historical/functional loss is represented by ROUTE_LOSS;
- surviving physical remnants remain truthfully DAMAGED.

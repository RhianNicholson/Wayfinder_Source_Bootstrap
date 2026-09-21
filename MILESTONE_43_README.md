# Milestone 43 — Route-Loss Physicalization Truth

M42 completed the region-aware historical scope path.

M43 fixes the recovery/completion semantic exposed by the approved ruin model.

## The problem

A completed ROUTE_LOSS intentionally preserves authentic Watchtower remnants.
The resulting materialization record can therefore remain:

```text
DAMAGED
```

rather than:

```text
LOST
```

The older `HistoricalPhysicalizationPlanner` predates that rule and considers a
non-LOST record potentially pending. Re-running historical generation could
therefore repeatedly reach the route-loss applier even after the ruin had
already been expressed.

## The rule

Historical state and physical state describe different truths:

```text
ROUTE_LOSS = historical / functional truth
DAMAGED    = surviving physical truth
```

Neither should be falsified to make the other easier to track.

M43 adds `MinecraftRouteLossPhysicalizationInspector`.

It derives completion from the original materialization record plus the approved
`RuinRemnantPolicy`:

1. Identify cells explicitly allowed to survive.
2. Ignore those cells.
3. Inspect every original non-survivor cell.
4. If any still matches its recorded material, physicalization remains pending.
5. If none match, the route-loss consequence is already physically expressed.

This also respects player modification. A player-created replacement block does
not become historical evidence merely because it occupies an old coordinate.

## Live validation

Use a world with an existing ROUTE_LOSS ruin.

Run:

```text
/wayfinder generatehistory
```

After the ruin has already been physically expressed, expected output includes:

```text
eventCommitted=false
physicalized=false
removedBlocks=0
```

The approved foundation/support remnants should remain in the world.

If testing a fresh eligible intact tower and the deterministic decision commits
ROUTE_LOSS, the first pass may report:

```text
eventCommitted=true
physicalized=true
removedBlocks=>0
```

Running the command again must not re-physicalize the same ruin.

## Build

```powershell
.\gradlew.bat clean test
.\gradlew.bat build
.\gradlew.bat runClient
```

## Next

M44 should update discovery-sequence semantics to understand the same distinction:
an intact SIGHT tower requires physical INTACT truth, while a ROUTE_LOSS with
DAMAGED or LOST physical state represents the broken-continuation discovery beat.

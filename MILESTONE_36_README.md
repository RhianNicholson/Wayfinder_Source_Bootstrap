# Milestone 36 — Automatic Historical Physicalization Boundary

M35 made procedural history commit-able. M36 adds the recovery-safe boundary
between committed historical truth and Minecraft block mutation.

The new `HistoricalPhysicalizationPlanner` asks one question:

> Does an already-committed ROUTE_LOSS event still have a physical
> materialization that has not reached LOST?

If yes, it produces physical work for that node.

This gives the Minecraft integration layer a safe ordering:

```text
load civilization/materialization/history
        ↓
generate + commit historical event
        ↓
SAVE HISTORY
        ↓
HistoricalPhysicalizationPlanner
        ↓
apply M32 ruin transformation
        ↓
inspect/update materialization state
        ↓
SAVE MATERIALIZATION
```

The important recovery property is that physicalization is derived from
persistent truth. If Minecraft stops after the history event is saved but
before the ruin transformation finishes, the next pass sees the same committed
ROUTE_LOSS and requests the physical consequence again.

No Minecraft runtime dependency is introduced into the history domain.

## Test

Overlay after M35.1:

```powershell
.\gradlew.bat clean test
.\gradlew.bat build
```

No runClient test is required for this first M36 slice.

## Next

M36.1 / the next integration slice will wire this planner into the live
Minecraft command/orchestration boundary using the existing repositories,
`MinecraftRouteLossApplier`, and materialization persistence. That is where the
seed-driven event becomes visibly automatic in-world.

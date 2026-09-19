# Milestone 35 — Historical Generation Transition

M35 connects the historical domain pipeline end-to-end:

```text
committed civilization/materialization truth
        ↓
procedural candidate discovery
        ↓
scoped deterministic decision
        ↓
route-loss validation
        ↓
append-only HistoricalEvent commit
```

The service remains domain-only. It does not remove Minecraft blocks and does
not update materialization condition itself.

That separation is deliberate:

**History is committed before the physical world responds to history.**

If the deterministic decision says no event occurred, state is unchanged.
If validation rejects the selected event, state is unchanged.
If event commit fails, state is unchanged.

Only an accepted event produces a new HistoricalEventState.

The event cause records the era key so generated history remains inspectable.

## Test

Overlay after M34:

```powershell
.\gradlew.bat clean test
.\gradlew.bat build
```

No runClient test is required.

## Next

After M35 validates, M36 can introduce the Minecraft-side historical generation
orchestrator:

1. load persistent truth;
2. run this transition;
3. persist the accepted historical event;
4. only then apply the M32 ruin transformation;
5. mark the materialization LOST;
6. support recovery when an event exists but physical application was
   interrupted.

That will be the first automatic seed-driven historical event capable of
changing the physical Wayfinder world.

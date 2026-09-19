# Wayfinder Milestone 22 — Persistent Materialization Truth

Milestone 22 creates a second persistent history layer.

Civilization state answers **why a structure belonged there**.

Materialization state answers **what was physically built there**.

These are deliberately separate SavedData stores.

## Persistent record

After a future placement commit, a materialization record can preserve:

- source civilization NodeId
- structure archetype
- materialization format version
- palette version
- physical condition
- exact original world cells
- semantic material role for every cell
- structural function for every cell

The original cell list is immutable historical evidence.

## Physical condition

```text
INTACT
DAMAGED
LOST
```

Absence of a record means:

```text
NEVER MATERIALIZED
```

This avoids encoding "never existed" as a fake historical state.

## Critical invariant

A later placement attempt cannot replace an existing materialization record with
new geometry.

Damage may change the condition.

Damage may not rewrite the original structure.

## Persistence

Materialization truth uses its own Minecraft SavedData:

```text
wayfinder:materialization
```

It is not embedded in civilization SavedData.

This preserves the architectural boundary:

```text
Civilization truth     Physical truth
why / relationships    exact built evidence
```

## Scope

Milestone 22 establishes and tests the persistence model only.

It intentionally does NOT yet modify Shrine or Watchtower placement commands.
That integration should happen only after this storage contract compiles and
passes independently.

## Test

Overlay after Milestone 21:

```powershell
.\\gradlew.bat clean test
.\\gradlew.bat build
```

No runClient test is required for this milestone.

After validation, Milestone 23 can atomically connect successful placement to
materialization persistence and add condition inspection against the recorded
original geometry.

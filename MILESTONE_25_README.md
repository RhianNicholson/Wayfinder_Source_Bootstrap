# Wayfinder Milestone 25 — Guarded Physical Route Loss

Milestone 25 makes the first persistent historical loss physically visible.

New command:

```text
/wayfinder routeloss
```

The command targets the nearest non-lost persistent Wayfinder materialization.

## Mutation order

The order is intentionally strict:

```text
validate recorded materialization
        ↓
create ROUTE_LOSS
        ↓
commit + persist historical event
        ↓
remove matching recorded Wayfinder cells
        ↓
persist materialization condition LOST
```

History is persisted **before** blocks are removed.

This prevents a physically missing structure from existing without an
authoritative historical explanation.

## Guarded destruction

The route-loss applier will remove only cells that still exactly match the
recorded Wayfinder palette.

If a player has replaced a cell with another block, that block is not deleted.

Historical events therefore have authority over Wayfinder-generated evidence,
not arbitrary player construction.

## What remains after loss

The following remain untouched:

- civilization NodeId
- node purpose
- geographic/civilization target
- civilization relationships
- original materialization geometry
- historical ROUTE_LOSS event

Only surviving matching physical cells are removed.

The resulting materialization condition is persisted as:

```text
LOST
```

This implements the design rule:

> The network describes what happened, not what should still be happening.

## Test

Overlay after Milestone 24:

```powershell
.\\gradlew.bat clean test
.\\gradlew.bat build
.\\gradlew.bat runClient
```

Use a world containing a Milestone 23+ recorded Shrine or Watchtower.

Stand closest to the structure you intend to lose and run:

```text
/wayfinder materializationstatus
```

Confirm it is the expected target.

Then:

```text
/wayfinder routeloss
```

Expected:

```text
Wayfinder route loss: APPLIED ... condition LOST ... civilization truth preserved
```

Then:

```text
/wayfinder materializationstatus
```

It should report:

```text
LOST
```

Re-run:

```text
/wayfinder routeloss
```

That same lost structure must not be selected again.

## Important scope note

Milestone 25 is a developer-facing historical event trigger.

It is not yet procedural history generation.

The next milestone should use this transition as a trusted primitive for the
first designed broken continuation in the discovery chain, rather than making
random structures disappear.

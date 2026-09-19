# Wayfinder Milestone 23 — Placement Persistence + Physical Condition

Milestone 22 established persistent materialization truth.

Milestone 23 connects future successful placement to that truth and adds
world-vs-history inspection.

## Placement persistence

After a Shrine or Watchtower renderer successfully reaches either:

```text
READY -> PLACED
ALREADY_PRESENT
```

the exact adapted geometry is recorded in:

```text
wayfinder:materialization
```

The record is append-only by source NodeId.

A later placement attempt cannot replace its original cells.

Rejected, blocked, or partial structures are not recorded as successful
materializations.

## Physical condition inspection

New command:

```text
/wayfinder materializationstatus
```

The command selects the nearest persistent materialization record and compares
every original recorded cell to the actual Minecraft block at that coordinate.

Classification:

```text
all cells match  -> INTACT
some cells match -> DAMAGED
zero cells match -> LOST
```

If the observed condition differs from the persisted condition, the condition is
updated while the original cells remain immutable.

## Important legacy rule

Shrines/Watchtowers manually placed before Milestone 22 do not have persistent
materialization records.

Milestone 23 does not silently fabricate those records.

Existing test structures therefore remain "legacy physical structures" until a
future explicit adoption/migration mechanism is introduced.

This is intentional. Reconstructing old physical history from already-mutated
terrain would violate:

> Persist truth. Derive convenience.

## Test procedure

Overlay after Milestone 22:

```powershell
.\gradlew.bat clean test
.\gradlew.bat build
```

For a full live persistence test, use a fresh Wayfinder world or a new committed
structure that has not already been physically placed:

```text
/wayfinder shrineplace
or
/wayfinder towerplace

/wayfinder materializationstatus
```

The first status should report:

```text
INTACT
```

Remove one recorded upper block and run:

```text
/wayfinder materializationstatus
```

Expected:

```text
DAMAGED
```

Remove all recorded structure blocks and run again.

Expected:

```text
LOST
```

No inspection operation repairs or regenerates blocks.

## Next step

Once this persistence/inspection boundary is validated, Wayfinder can safely
introduce historical destruction events and the first intentionally broken
continuation without confusing "physically gone" with "never existed."

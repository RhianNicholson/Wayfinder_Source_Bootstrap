# Wayfinder Milestone 24 — Historical Route-Loss Event

Milestone 24 introduces the first persistent historical event.

The first event type is:

```text
ROUTE_LOSS
```

This is the historical statement:

> A valid part of the Wayfinder network once existed, and later ceased to
> function or survive.

It is intentionally **not** the statement:

> This node should no longer exist in civilization state.

## Three truths now remain separate

```text
Civilization Truth
Why the structure belonged in the network

Materialization Truth
What was physically built

Historical Event Truth
What happened to it later
```

A lost structure therefore retains its civilization NodeId and relationships.

The network describes what happened, not merely what survives.

## Append-only event log

Events have:

- deterministic event ID
- strict sequence number
- event type
- affected NodeId
- durable cause string

The event store is:

```text
wayfinder:history_events
```

The sequence is contiguous and append-only.

A route-loss event for the same affected node is deterministic and cannot be
silently recommitted with a new explanation.

## Why blocks are NOT removed in Milestone 24

Historical causality must exist before physical destruction.

The correct later transition is:

```text
validate loss event
        ↓
commit historical event
        ↓
apply physical loss
        ↓
inspect materialization
        ↓
condition becomes LOST
```

If block deletion happened first and persistence failed, Minecraft would contain
a ruin with no authoritative explanation.

Milestone 24 therefore establishes and validates the event truth first.

## Test

Overlay after Milestone 23:

```powershell
.\\gradlew.bat clean test
.\\gradlew.bat build
```

No runClient test is required.

## Next milestone

Milestone 25 can add a guarded historical-loss transition that targets a real
recorded Wayfinder materialization, commits ROUTE_LOSS first, and only then
removes a controlled subset or all of its physical cells.

That is where the first genuinely broken continuation becomes visible in the
world.

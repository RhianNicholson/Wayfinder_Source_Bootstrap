# Milestone 38 — Live Generate-History Command

M37.1 validated the Minecraft historical orchestrator. M38 adds the command
adapter that invokes one deterministic historical-generation pass.

The command body is intentionally isolated in `HistoricalGenerationCommand` so
the existing `WayfinderCommands` tree needs only one registration line.

Add/import:

```java
import com.wayfinder.minecraft.command.HistoricalGenerationCommand;
```

Under the existing `wayfinder` literal, register:

```java
.then(Commands.literal("generatehistory")
    .requires(source -> source.hasPermission(2))
    .executes(HistoricalGenerationCommand::execute))
```

If your current command root already uses a different permission convention,
keep that convention; the execution adapter itself is unchanged.

The first live slice uses stable scope:

- region: `global`
- era: `era:1`
- generation version: `1`

This is deliberate. We are validating deterministic live behavior before
binding history to the full semantic-region/era lifecycle.

## Test

```powershell
.\gradlew.bat clean test
.\gradlew.bat build
.\gradlew.bat runClient
```

In a world with a committed/materialized Shrine -> Watchtower chain:

```text
/wayfinder generatehistory
```

Possible deterministic outcomes:

1. `eventCommitted=false` — the scoped historical roll says no route loss
   occurred for this seed/scope.
2. `eventCommitted=true` and `physicalized=true` — ROUTE_LOSS was committed,
   history was persisted first, then the Watchtower was transformed to its M32
   ruin state.
3. `eventCommitted=false` but `physicalized=true` — recovery path: a previously
   committed event still required its physical consequence.

Run the command repeatedly in the same world. It must not keep inventing new
loss events for the same already-lost continuation.

Then inspect:

```text
/wayfinder discoverystatus
/wayfinder materializationstatus
```

For a route-loss ruin, discovery should remain coherent and the physical record
should reflect the surviving remnant rather than pretending the blocks vanished.

## Architectural checkpoint

The command is a developer diagnostic, not player-facing gameplay. Later the
same orchestrator will be invoked by semantic-region/era generation hooks.

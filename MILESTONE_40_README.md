# Milestone 40 — Live Regional Historical Scope

M39 introduced stable historical region and era identities. M40 wires those
identities into the already-validated `/wayfinder generatehistory` path.

The command no longer sends:

```text
region = global
era = era:1
```

Instead it derives the historical region from the command source's world
position and sends the resulting `HistoricalScope` to the Minecraft historical
orchestrator.

Era remains explicitly civilization-owned:

```text
CURRENT_ERA = 1
```

It is deliberately **not** derived from Minecraft day/time.

The command response now includes the actual scope, for example:

```text
Wayfinder history | region=wayfinder-region:0:-1 |
era=wayfinder-era:1 | ...
```

This makes deterministic behavior directly testable.

## Test

Overlay after M39:

```powershell
.\gradlew.bat clean test
.\gradlew.bat build
.\gradlew.bat runClient
```

Run:

```text
/wayfinder generatehistory
```

Record the reported region, era, roll and threshold.

Run it again from another position inside the same 512x512 region. The region
identity must remain the same.

Cross a 512-block region boundary and run it again. The reported region identity
must change, giving that region an independent deterministic historical stream.

The command remains developer instrumentation. It does not expose region/era
metadata to normal players.

## Next

M41 should stop treating all civilization truth as globally eligible during a
regional historical pass. Candidate discovery will be constrained to the
selected historical region, so Region A's history cannot destroy a structure
belonging to Region B.

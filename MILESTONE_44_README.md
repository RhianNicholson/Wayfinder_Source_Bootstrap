# Milestone 44 — Discovery Ruin Semantics

M44 aligns discovery diagnostics with the approved ruin model.

- `SIGHT_TOWER_PRESENT` now requires an `INTACT` observation materialization.
- `BROKEN_CONTINUATION_PRESENT` now requires `ROUTE_LOSS` plus a non-`INTACT`
  affected materialization (`DAMAGED` or `LOST`).
- The discovery sequence remains coherent through either the intact tower path
  or the historically broken continuation path.

Apply after M43.1 and run:

```powershell
.\gradlew.bat clean test
.\gradlew.bat build
.\gradlew.bat runClient
```

Then test `/wayfinder discoverystatus` in an intact chain and, if available, an
existing route-loss ruin. The intact chain should report `SIGHT_TOWER_PRESENT`;
the ruin should report `BROKEN_CONTINUATION_PRESENT`.

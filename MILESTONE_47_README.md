# Milestone 47 — Historical Event Handler Dispatch

Adds the event-specific execution boundary:

`HistoricalEventOpportunity -> deterministic selection -> handler registry -> event-specific validation -> factory -> append-only commit`

New types:
- `HistoricalEventHandler`
- `HistoricalEventHandlerResult`
- `HistoricalEventHandlerRegistry`
- `RouteLossHistoricalEventHandler`

ROUTE_LOSS keeps its existing transition validation, factory and commit behavior.
Physical consequences remain outside this boundary.

M47 does not yet replace the live generation path.

Apply after M46:

```powershell
.\gradlew.bat clean test
.\gradlew.bat build
```

No runClient test is required. M48 will compose providers, generic selection and
handler dispatch into the generalized historical-generation service.

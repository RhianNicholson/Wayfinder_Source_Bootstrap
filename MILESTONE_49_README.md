# Milestone 49 — Event Occurrence Decision Boundary

M48 composed generic opportunity discovery, selection and event dispatch.
M49 restores the second decision that must not be lost during generalization:

```text
What plausible event is being considered?
                ↓
       opportunity selection
                ↓
Did that selected event actually occur?
                ↓
      occurrence decision
                ↓
       validate → commit
```

New types:

- `HistoricalEventOccurrenceDecision`
- `HistoricalEventOccurrenceDecider`
- `HistoricalEventOccurrenceRegistry`
- `RouteLossOccurrenceDecider`

For ROUTE_LOSS, the opportunity weight is its validated vulnerability and is
therefore the occurrence threshold.

The RNG stream is independently scoped to
`historical-route-loss-occurrence`, keeping occurrence separate from generic
opportunity selection.

M49 introduces the boundary only. The existing live generator and the M48
generalized service are unchanged.

Apply after M48:

```powershell
.\gradlew.bat clean test
.\gradlew.bat build
```

No runClient test is required.

M50 will integrate the occurrence registry into the generalized generation
service and expose both selection and occurrence diagnostics so we can compare
the generalized path against the existing ROUTE_LOSS pipeline before switching
live generation.

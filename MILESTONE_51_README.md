# Milestone 51 — Live Generalized History Migration

M51 switches the Minecraft historical orchestrator from the specialized
`HistoricalGenerationService` to the generalized M45–M50 pipeline.

Live composition is now:

```text
RouteLossOpportunityProvider
        ↓
DeterministicHistoricalOpportunitySelector
        ↓
RouteLossOccurrenceDecider
        ↓
RouteLossHistoricalEventHandler
        ↓
persist committed history
        ↓
recovery-safe physicalization
```

The existing M43 exact-block physicalization truth check remains intact.

The `/wayfinder generatehistory` output keeps its existing `roll` and
`threshold` fields. They now report the generalized event-specific occurrence
decision. If there is no selected opportunity/occurrence decision they remain
the familiar sentinel `1.0000 / 0.0000`.

## Important deterministic note

The generalized architecture intentionally separates opportunity selection from
event occurrence into independently scoped random streams. Therefore numeric
rolls need not equal those produced by the retired specialized generator for a
given old test world. The parity requirement is semantic:

- only justified regional candidates can participate;
- no opportunity means no invented event;
- occurrence is deterministic for the same scope/state;
- committed events persist;
- route loss physically expresses once;
- approved remnants survive;
- repeated runs do not duplicate or re-physicalize the event.

## Validation

Apply after M50:

```powershell
.\gradlew.bat clean test
.\gradlew.bat build
.\gradlew.bat runClient
```

Then run:

```text
/wayfinder generatehistory
```

Recommended checks:

1. Existing already-lost/otherwise ineligible state:
   `eventCommitted=false`, `physicalized=false`, `removedBlocks=0`.
2. Fresh eligible intact chain:
   output should contain a real occurrence roll/threshold if selected.
3. If ROUTE_LOSS occurs, run the command again:
   it must not duplicate or re-physicalize the same ruin.
4. `/wayfinder discoverystatus` should continue distinguishing intact SIGHT
   towers from broken continuations.

## Next

After M51 live parity is validated, M52 can retire the specialized generation
implementation/compatibility scaffolding and begin the civilization-era
lifecycle work on top of one canonical history engine.

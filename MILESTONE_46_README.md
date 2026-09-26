# Milestone 46 — Deterministic Historical Opportunity Selection

M45 introduced the generic historical opportunity boundary. M46 adds a generic,
deterministic weighted selector across those opportunities.

## Rule preserved

```text
Eligibility creates possibilities.
Randomness chooses between possibilities.
Randomness never creates possibilities.
```

The selector receives only prevalidated `HistoricalEventOpportunity` values.

It then:

1. deterministically orders them;
2. sums positive weights;
3. creates a scoped random stream using world seed, region, era, subsystem stage,
   and generation version;
4. performs weighted selection;
5. returns the selected opportunity plus its selection roll.

Empty opportunity lists produce no selection.

## Important behavior boundary

M46 still does not replace the validated ROUTE_LOSS generation pipeline.
This is deliberate. We are validating generic selection independently before
M47 connects selection to event-specific decision/validation/commit behavior.

## Build

Apply after M45.1:

```powershell
.\gradlew.bat clean test
.\gradlew.bat build
```

No runClient test is required.

## Next

M47 will introduce event-type dispatch after generic opportunity selection.
ROUTE_LOSS will be the first registered handler and will retain its existing
occurrence threshold, transition validation, event creation, and commit rules.

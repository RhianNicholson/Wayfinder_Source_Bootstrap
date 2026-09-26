# Milestone 45 — Historical Event Opportunity Boundary

M44 aligned discovery truth with historical ruin truth.

M45 begins generalizing historical generation beyond a single event type
without changing the validated ROUTE_LOSS behavior.

## New boundary

```text
Civilization + Materialization + History + HistoricalScope
                         ↓
          Event-specific eligibility logic
                         ↓
             HistoricalEventOpportunity
                         ↓
               deterministic choice
                         ↓
              validate → commit
```

`HistoricalEventOpportunity` contains:

- event type
- affected node
- positive selection weight
- reason the opportunity exists

`HistoricalEventOpportunityProvider` is the generic contract.

`RouteLossOpportunityProvider` adapts the existing
`ProceduralRouteLossSelector`; it does not replace or alter its validated
eligibility rules.

This preserves the core rule:

> Randomness chooses between possibilities. Randomness never creates possibilities.

## Important scope

M45 introduces the abstraction only. `HistoricalGenerationService` continues
using the existing ROUTE_LOSS path, so this milestone should produce no live
behavior change.

That makes the architectural change independently testable before we migrate
event selection in M46.

## Validation

Overlay after M44.1:

```powershell
.\gradlew.bat clean test
.\gradlew.bat build
```

No runClient test is required because the live generation path is intentionally
unchanged.

## Next

M46 will introduce deterministic selection across generic historical
opportunities while retaining event-specific validation and commit behavior.

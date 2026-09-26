# Milestone 50 — Generalized Occurrence Integration

M50 completes the generalized domain pipeline by integrating the occurrence
boundary introduced in M49.

The generalized service now executes:

```text
opportunity providers
        ↓
plausible opportunities
        ↓
deterministic opportunity selection
        ↓
event-specific occurrence decider
        ↓
occurs? ── no → unchanged history
        │
       yes
        ↓
event-specific handler
        ↓
validate → create → commit
```

`GeneralizedHistoricalGenerationResult` now exposes both:

- `selectionDecision`
- optional `occurrenceDecision`

along with the optional committed event.

This gives the next parity milestone enough diagnostic information to compare
the generalized pipeline with the existing specialized ROUTE_LOSS path.

## Safety

The Minecraft orchestrator is still using the original
`HistoricalGenerationService`. M50 therefore changes no live world behavior.

## Validation

Apply after M49:

```powershell
.\gradlew.bat clean test
.\gradlew.bat build
```

No runClient test is required.

## Next

M51 will perform the parity/migration step: wire the generalized ROUTE_LOSS
provider, selector, occurrence decider and handler into the Minecraft
orchestrator, then validate live deterministic generation and physicalization
before retiring the specialized path.

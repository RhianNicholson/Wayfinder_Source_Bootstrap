# Milestone 48 — Generalized Historical Generation Service

M48 composes the generic pieces validated in M45–M47.

```text
HistoricalScope
+ civilization truth
+ materialization truth
+ committed history
        ↓
HistoricalEventOpportunityProvider(s)
        ↓
all plausible opportunities
        ↓
DeterministicHistoricalOpportunitySelector
        ↓
selected opportunity
        ↓
HistoricalEventHandlerRegistry
        ↓
event-specific validation + commit
        ↓
GeneralizedHistoricalGenerationResult
```

The service does not mutate Minecraft blocks. Physicalization remains a later
boundary after historical truth is committed.

## Behavioral safety

The existing `HistoricalGenerationService` is intentionally left untouched.
The live `/wayfinder generatehistory` path therefore continues using the
already-validated ROUTE_LOSS generator.

M48 establishes the generalized pipeline alongside it so we can verify parity
before replacing the live path.

At present the only provider/handler pair is ROUTE_LOSS:

- `RouteLossOpportunityProvider`
- `RouteLossHistoricalEventHandler`

## Important distinction

The generic opportunity selector chooses *which plausible event opportunity* is
considered. Event-specific occurrence rules are not silently invented here.

Before the generalized service becomes the live replacement, ROUTE_LOSS parity
must include its existing occurrence-threshold behavior. That migration belongs
in the next integration step rather than being hidden inside this composition
milestone.

## Validation

Apply after M47:

```powershell
.\gradlew.bat clean test
.\gradlew.bat build
```

No runClient test is required because the live path is unchanged.

## Next

M49 will add explicit event-specific occurrence decision handling to the generic
pipeline, beginning with ROUTE_LOSS. This preserves the validated distinction
between:

1. choosing among plausible historical opportunities, and
2. deciding whether the selected event actually occurs.

Only after parity is proven should the Minecraft orchestrator switch to the
generalized generator.

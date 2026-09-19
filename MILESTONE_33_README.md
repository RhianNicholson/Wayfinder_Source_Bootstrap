# Milestone 33 — Procedural Historical Route-Loss Candidates

Milestone 32 validated physical ruin remnants. M33 begins moving historical
loss away from debug-only initiation and into the civilization/history engine.

`ProceduralRouteLossSelector` discovers only real committed:

`DIRECTION -> DIRECTIONAL_REFERENCE -> OBSERVATION`

continuations whose destination was materialized, is not LOST, and has no
existing ROUTE_LOSS event.

It produces candidates only. It does not mutate the world.

This preserves the rule:

**Randomness chooses between possibilities. Randomness never creates possibilities.**

Distance contributes to preliminary vulnerability only after civilization truth
has established eligibility.

Run:

```powershell
.\gradlew.bat clean test
.\gradlew.bat build
```

No runClient test is required.

Next: connect eligible historical candidates to the existing scoped
deterministic RNG architecture so per-region/per-era history becomes repeatable.

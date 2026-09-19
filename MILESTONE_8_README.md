# Wayfinder Milestone 8 — Live Admission + Commit Model

This milestone introduces the first explicit commit boundary for Wayfinder
civilization state.

## Pipeline

`ObservationDecision`
→ `NodeProposal`
→ `Network Validation`
→ `NodeAdmissionDecision`
→ `NodeTransition`
→ `Transition Validation`
→ `Commit`
→ `CivilizationState`

## Important limitation

The state is intentionally in-memory only.

It resets when Minecraft restarts.

That is deliberate. Persistence comes next, after the commit model has been
tested in live worlds.

## New live output

`/wayfinder analyze` can now report:

- `OBSERVATION committed`
- `proposal rejected <REASON>`
- `no civilization decision`

The command also reports the current in-memory committed node count.

## Side-effect rule

Rejected proposals and rejected transitions do not mutate civilization state.

This enforces:

`Evaluate -> Produce Decision/Transition -> Validate -> Commit`

rather than allowing evaluation code to directly alter persistent truth.

## Stable node identity

Committed nodes receive a deterministic NodeId derived from the proposal key.

Re-evaluating the exact same proposal therefore produces the same logical node
identity and is rejected as a duplicate rather than creating a second copy.

## Tests

This patch verifies that:

1. a valid transition commits exactly one node
2. committing the same transition again is rejected
3. rejected commits return the original state unchanged

## Run

```powershell
.\gradlew.bat clean test
.\gradlew.bat build
.\gradlew.bat runClient
```

Then:

```text
/wayfinder analyze
```

Run it twice from the same general area.

Expected behavior:

- first qualifying run may report `OBSERVATION committed`
- a later equivalent proposal should be rejected by network/transition
  validation rather than creating duplicate history
- node count should remain stable when a duplicate is rejected

## Next milestone

Milestone 9 should replace the temporary static in-memory state with a proper
persistence boundary so committed civilization truth survives world restarts.

# Milestone 34 — Deterministic Historical Event Decision

M33 established which committed continuations are historically eligible for
route loss. M34 connects those possibilities to Wayfinder's scoped deterministic
random architecture.

The new decision service uses:

```text
world seed
+ semantic region key
+ historical era key
+ stage: historical-route-loss
+ generation version
```

to create an isolated random stream.

It then performs two distinct operations:

1. weighted selection among already-valid candidates;
2. an occurrence roll against the selected candidate's vulnerability.

This means a candidate with a higher vulnerability is more likely to be chosen,
but a high score is still not permission for the event to exist.

Most importantly:

- identical seed + region + era + version produces the identical decision;
- changing another subsystem's random calls cannot perturb historical loss;
- an empty candidate list can never produce an event;
- this milestone still does not mutate persistent history or Minecraft blocks.

This implements:

**Randomness chooses between possibilities. Randomness never creates possibilities.**

## Test

Overlay after M33:

```powershell
.\gradlew.bat clean test
.\gradlew.bat build
```

No `runClient` test is required.

## Next

Once M34 validates, the next boundary is a historical generation transition:
candidate discovery -> deterministic decision -> event validation -> append-only
commit. Physical ruin application remains downstream of committed history.

# Wayfinder Milestone 12 — Direction Node Decision

Milestone 12 creates the first decision pipeline whose reason for existing is
another committed Wayfinder node.

That is a major transition from isolated civilization decisions to actual
network growth.

## Starting truth

```text
OBSERVATION Node
       ↓
    Landmark
```

Milestone 12 asks:

> Where could the Wayfinders plausibly have created a directional node whose
> purpose was to lead a traveler toward that Observation Node?

## Pipeline

```text
Committed OBSERVATION Node
          ↓
DirectionCandidateGenerator
          ↓
hard validity
          ↓
reason evaluation
          ↓
scoring
          ↓
plausibility band
          ↓
deterministic selection
          ↓
DirectionDecision
```

## Candidate generation

The first coarse generator examines eight approach directions at four travel
distances:

```text
48, 80, 112, 144 blocks
```

This creates at most 32 opportunities around an Observation Node.

Water sites are discarded immediately.

Each remaining opportunity derives:

- buildability from local terrain variation
- travel fit from vertical cost over horizontal distance
- directional clarity from useful travel distance
- destination importance from the already committed Observation Node

## Hard validity

A candidate is not merely down-scored when it is impossible.

It is removed when:

- destination is not an OBSERVATION Node
- buildability is too poor
- distance is outside the directional operating range
- another committed civilization node already occupies the local site

This preserves the established rule:

> Candidate invalidity is hard rejection, not low score.

## Reasons

The primary reason is COMMUNICATION.

Supporting reasons are:

- TRAVEL
- CIVILIZATIONAL

The CIVILIZATIONAL reason is especially important: the Direction candidate
does not invent a destination. It exists because an Observation Node is
already committed historical truth.

## Score

First-pass Direction score:

```text
directional clarity       30%
travel fit                25%
buildability              20%
destination importance    25%
```

Plausibility uses the same conceptual rule as Observation decisions:

```text
absolute minimum: 0.55
relative band:    85% of best candidate
```

The selector is intentionally deterministic for now. Scoped weighted random
selection remains a later replacement after the full vertical-slice semantics
are stable.

## Important architecture boundary discovered here

`DirectionDecision` targets a `CivilizationNode`, not a `Landmark`.

The existing `NodeProposal`/`CivilizationNode` representation still assumes
every node's target is a natural Landmark because it originated with the
Observation vertical slice.

Milestone 12 deliberately does NOT fake a Landmark target for a Direction
Node.

That would violate the language rule:

> The Wayfinder language communicates relationships, not objects.

The next milestone should evolve node targeting into an explicit typed
reference so:

```text
OBSERVATION -> GeographicFeature
DIRECTION   -> CivilizationNode
```

are both represented truthfully.

Only after that type boundary is corrected should DirectionDecision be
admitted, committed, related, and persisted.

## Tests

Milestone 12 verifies:

1. a committed Observation Node can produce a Direction decision
2. a non-Observation destination cannot produce one
3. nearby committed nodes invalidate redundant Direction sites

## Build

Overlay after Milestone 11:

```powershell
.\gradlew.bat clean test
.\gradlew.bat build
```

Minecraft command behavior remains unchanged in this milestone.

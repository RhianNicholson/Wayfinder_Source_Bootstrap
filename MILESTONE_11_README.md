# Wayfinder Milestone 11 — Relationship Commit + Persistence

Milestone 11 gives Wayfinder relationships the same transactional guarantees
already established for Nodes.

## Pipeline

```text
RelationshipProposal
        ↓
Relationship Validation
        ↓
RelationshipAdmissionDecision
        ↓
RelationshipTransition
        ↓
Transition Validation
        ↓
Commit
        ↓
CivilizationState
        ↓
SavedData
```

## CivilizationState

Civilization truth now contains two immutable collections:

```text
nodes
relationships
```

The previous one-argument `CivilizationState(nodes)` constructor remains
available, so Milestone 8/9 code and tests remain source-compatible.

## Relationship commit guarantees

A relationship transition is rejected when:

- its source node no longer exists at commit time
- its target node no longer exists at commit time
- its deterministic RelationshipId already exists
- the source proposal has already been committed
- the same semantic edge already exists

Rejected transitions return the exact original CivilizationState instance.

Evaluation still cannot mutate truth directly.

## Persistence

`wayfinder:civilization` SavedData now stores an optional:

```text
relationships
```

collection in addition to `nodes`.

This is deliberately backward-compatible with Milestone 9 worlds. Old worlds
without a `relationships` field deserialize it as an empty list.

No migration step is required for this additive schema change.

## Persisted relationship truth

Each relationship stores:

- RelationshipId
- RelationshipType
- source NodeId
- target NodeId
- score
- primary/supporting reasons
- source proposal key

## Tests

Milestone 11 verifies that:

1. a valid relationship commits exactly once
2. committing the same relationship again is rejected without state mutation
3. relationship persistence round-trips without losing historical truth
4. existing Milestone 9 node-only persistence APIs remain compatible

## Build

Overlay after Milestone 10:

```powershell
.\gradlew.bat clean test
.\gradlew.bat build
```

Minecraft command behavior is intentionally unchanged because the world does not
yet have a generated DIRECTION Node to connect to the persisted OBSERVATION Node.

## Next milestone

The next step is the first **Direction Node candidate/decision pipeline**.

It will begin with an existing committed OBSERVATION Node and ask:

> Where could the Wayfinders plausibly have built a directional node whose
> architecture and clue relationship intentionally lead toward this
> observation site?

That will give us:

```text
DIRECTION Node
      ↓
DIRECTIONAL_REFERENCE
      ↓
OBSERVATION Node
      ↓
Landmark
```

Only after that logical chain is valid and persistent should we instantiate
the physical Shrine and Watchtower.

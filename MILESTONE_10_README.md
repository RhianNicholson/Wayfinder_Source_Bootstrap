# Wayfinder Milestone 10 — Relationship Model + Validation

Milestone 10 makes relationships between civilization nodes explicit domain
objects.

No relationship is committed or persisted yet.

That is intentional.

The purpose of this milestone is to prove the semantic and validation contract
before allowing relationships to mutate historical truth.

## First supported vertical-slice relationship

```text
DIRECTION Node
      |
      | DIRECTIONAL_REFERENCE
      v
OBSERVATION Node
```

This is the logical relationship that will eventually be expressed physically
as:

```text
Waystone Shrine -> Watchtower
```

The structures still do not exist yet. The engine is learning the civilization
relationship first.

## New domain objects

- `RelationshipType`
- `RelationshipProposal`
- `CivilizationRelationship`
- `RelationshipValidationContext`
- `RelationshipProposalValidator`
- `RelationshipIdentityFactory`

## Hard validation rules

A relationship is rejected if:

- source node does not exist
- target node does not exist
- source targets itself
- source/target purposes do not match relationship semantics
- an equivalent relationship already exists
- no meaningful civilization/communication/travel reason exists
- score falls below the admission threshold

For `DIRECTIONAL_REFERENCE`, the first valid purpose pairing is deliberately:

```text
DIRECTION -> OBSERVATION
```

Not the reverse.

## Stable identity

Relationship identity is deterministic from:

- relationship type
- source NodeId
- target NodeId
- proposal key

This means the same historical relationship resolves to the same logical
identity.

## Tests

Milestone 10 proves:

1. DIRECTION -> OBSERVATION is valid
2. OBSERVATION -> DIRECTION is invalid for a directional reference
3. relationships cannot point to missing nodes
4. relationship identity is deterministic

## Run

```powershell
.\gradlew.bat clean test
.\gradlew.bat build
```

Minecraft command behavior should remain unchanged in this milestone.

## Next milestone

Once this passes:

```text
RelationshipProposal
      ↓
Relationship Validation
      ↓
Relationship Transition
      ↓
Transition Validation
      ↓
Commit
      ↓
Persisted CivilizationRelationship
```

Only then will a relationship become authoritative Wayfinder history.

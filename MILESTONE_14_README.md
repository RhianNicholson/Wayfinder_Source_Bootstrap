# Wayfinder Milestone 14 — Live Direction Chain

Milestone 14 is the first time the civilization engine grows because of its own
already committed history.

## Live chain

```text
DIRECTION Node
      │
      │ DIRECTIONAL_REFERENCE
      ▼
OBSERVATION Node
      │
      ▼
Landmark
```

The Direction Node targets the Observation Node through the typed target model
introduced in Milestone 13.

The explicit relationship records the semantic edge independently.

## Transaction boundary

A Direction node is staged first, but the command persists the new state only
after its required DIRECTIONAL_REFERENCE relationship also validates and
commits.

If any stage fails, the original persisted civilization state remains
authoritative.

Pipeline:

```text
DirectionDecision
      ↓
typed NodeProposal
      ↓
Network validation
      ↓
Node admission
      ↓
Node transition
      ↓
staged node commit
      ↓
DirectionalReferenceProposal
      ↓
Relationship validation
      ↓
Relationship transition
      ↓
relationship commit
      ↓
persist complete chain
```

This follows the project rule:

> Evaluate → Produce Decision/Transition → Validate → Commit

## State-integrity fix

Milestone 14 also fixes an older assumption in NodeCommitService.

Before relationships existed, a node commit could safely construct:

```text
CivilizationState(nextNodes)
```

After Milestone 11 that would silently discard existing relationships.

It now constructs:

```text
CivilizationState(nextNodes, current.relationships())
```

A regression test locks this behavior down.

## Live `/wayfinder analyze`

When the current primary landmark has a committed Observation Node, the command
now attempts a Direction decision.

On success it reports approximately:

```text
DIRECTION chain committed
nodes 2
relationships 1
```

The exact counts can be higher in a world with existing Wayfinder history.

The command does not place blocks yet. This milestone commits civilization
truth only.

## Why no Shrine yet?

We now have the semantic historical fact needed to justify a Shrine:

```text
A Wayfinder Direction node existed here specifically to communicate a route
toward that Observation node.
```

Physical generation can therefore be downstream of civilization truth rather
than inventing it.

## Tests

Milestone 14 verifies:

1. adding a node preserves already committed relationships
2. DirectionDecision can commit a DIRECTION Node
3. the Direction Node targets the Observation Node
4. DIRECTIONAL_REFERENCE commits in the same operation
5. source and target IDs on the relationship are correct

## Run

Overlay after Milestone 13.2:

```powershell
.\gradlew.bat clean test
.\gradlew.bat build
.\gradlew.bat runClient
```

Then run:

```text
/wayfinder analyze
```

For a suitable already-analyzed area, look for:

```text
DIRECTION chain committed
```

and a relationship count greater than zero.

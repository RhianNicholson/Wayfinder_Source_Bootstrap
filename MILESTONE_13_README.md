# Wayfinder Milestone 13 — Typed Node Targets

Milestone 13 removes a foundational shortcut from the first Observation-node
implementation.

Previously:

```text
CivilizationNode.target == Landmark
```

That happened because Observation Nodes were the only committed node type.

It is no longer truthful once Direction Nodes exist.

## New model

```text
NodeTarget
├── GeographicNodeTarget
└── CivilizationNodeTarget
```

This gives us:

```text
OBSERVATION ──targets──> GeographicNodeTarget(Landmark)

DIRECTION   ──targets──> CivilizationNodeTarget(NodeId)
```

A Direction Node no longer needs a fake landmark merely to satisfy an old data
shape.

## Domain invariants

The first semantic constraints are now enforced at construction time:

```text
OBSERVATION requires GeographicNodeTarget
DIRECTION   requires CivilizationNodeTarget
```

That means an invalid target type cannot accidentally become committed history.

## Compatibility

Existing Java call sites that construct Observation Nodes or proposals using a
Landmark remain source compatible through convenience constructors.

Milestone 9 Observation data also remains compatible on disk.

The old SavedData field:

```text
target
```

continues to mean the geographic target.

The new optional field:

```text
target_node_id
```

represents a civilization-node target.

Exactly one must be present for each persisted node.

## NodeProposalFactory

The factory can now truthfully create both:

```text
ObservationDecision
    -> NodeProposal(GeographicNodeTarget)

DirectionDecision
    -> NodeProposal(CivilizationNodeTarget)
```

This connects Milestone 12's Direction decision to the general node-admission
pipeline without inventing semantic data.

## Tests

Milestone 13 verifies:

1. Observation Nodes use geographic targets
2. Direction Nodes use civilization-node targets
3. Direction Nodes cannot masquerade a Landmark as their semantic target
4. both target types round-trip through persistence

## Build

Overlay after Milestone 12:

```powershell
.\gradlew.bat clean test
.\gradlew.bat build
```

Minecraft behavior remains intentionally unchanged.

## Next milestone

Milestone 14 can now safely wire:

```text
DirectionDecision
      ↓
typed NodeProposal
      ↓
Network Validation
      ↓
Node Admission
      ↓
Node Transition
      ↓
Commit
      ↓
DIRECTION Node
      ↓
DIRECTIONAL_REFERENCE
      ↓
OBSERVATION Node
```

Then both the node and its relationship can be saved as authoritative history.

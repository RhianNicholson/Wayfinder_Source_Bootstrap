# Wayfinder Milestone 7 — Node Proposal + Network Validation

This milestone creates the boundary between a civilization **decision** and
civilization **history**.

Nothing is persisted and no blocks are placed.

## Pipeline

`ObservationDecision`
→ `NodeProposal`
→ `Network Validation`
→ `NodeAdmissionDecision`

A successful observation decision is therefore no longer automatically allowed
to become a Wayfinder Node.

## Network admission checks

The first validator checks:

- minimum proposal quality
- presence of a reason appropriate to the node purpose
- duplicate local sites
- redundant nearby Observation Nodes

Initial development thresholds:

- minimum proposal score: `0.55`
- duplicate radius: `12 blocks`
- observation redundancy radius: `48 blocks`

These values are isolated constructor parameters and can be tuned later.

## Why this layer exists

Geography answers:

> Could this place support observation?

The civilization decision answers:

> Would this place have been useful to the Wayfinders?

Network validation answers:

> Does this proposed place deserve to exist as part of the civilization?

That distinction preserves the Golden Rule:

> No structure may exist without a reason.

and prevents individually good locations from producing a noisy, redundant
network.

## Tests

This patch adds tests proving that network validation:

1. accepts a purposeful Observation proposal in an empty network
2. rejects a redundant nearby Observation Node
3. rejects a proposal below the network admission score

## Run

```powershell
.\gradlew.bat clean test
.\gradlew.bat build
```

Minecraft behavior should remain unchanged in this milestone because this domain
layer is not yet committed or exposed through the command.

## Next milestone

After this passes, wire the live `ObservationDecision` into
`NodeAdmissionService` and expose the result in `/wayfinder analyze`.

Then implement the commit boundary:

`NodeAdmissionDecision -> validate transition -> commit OBSERVATION Node`

Persistence comes only after that commit model is stable.

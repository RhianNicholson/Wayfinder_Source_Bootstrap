# Wayfinder Milestone 5 — First Civilization Decision

This patch introduces the first civilization-layer decision pipeline.

It does **not** place a Watchtower and it does **not** persist a Node yet.

The new pipeline is:

`observation sites -> candidate generation -> validity -> reason -> scoring -> plausibility band -> deterministic selection -> ObservationDecision`

## Why this matters

Until now Wayfinder has only observed and interpreted terrain.

With this milestone, it can form the first defensible civilization proposal:

> This site is a plausible place from which the Wayfinders would have observed this landmark.

That is intentionally still a proposal, not history.

## New packages

- `civilization.model`
- `civilization.candidate`
- `civilization.reason`
- `civilization.scoring`
- `civilization.selection`
- `civilization.service`

## Initial scoring

Observation candidates currently weight:

- sightline visibility: 40%
- landmark salience: 25%
- buildability: 20%
- terrain fit: 15%

These are prototype weights and are isolated for later tuning.

## Plausibility

The prototype filter uses the design values:

- absolute minimum: 0.55
- relative band: best score * 0.85

Only candidates satisfying both may compete.

## Selection

This first patch uses deterministic best-score selection with a stable candidate-key tie break.

That is deliberate for the first live civilization test. The selector is isolated behind its own class so the next step can replace it with the already-designed scoped weighted-random selection without changing the rest of the pipeline.

## Integration step

The next small integration change should instantiate `ObservationDecisionService` from the live `/wayfinder analyze` flow and report:

- generated candidate count
- valid candidate count
- plausible candidate count
- selected site
- selected score
- primary and supporting reasons

Once that output looks believable in real terrain, the selected decision can become a `NodeProposal`, pass network validation, and eventually be committed as the first persistent Wayfinder Node.

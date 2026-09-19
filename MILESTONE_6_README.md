# Wayfinder Milestone 6 — Live Civilization Decision

This patch connects the live `/wayfinder analyze` flow to the civilization-layer
`ObservationDecisionService`.

It still does **not** place blocks and does **not** persist civilization state.

## New live behavior

If terrain analysis finds a primary landmark and a plausible observation
decision, the command now reports something like:

`Wayfinder: MOUNTAINOUS | high points 7 | landmarks 3 | observation sites 6 | primary PROMINENT_PEAK 0.86 | OBSERVATION selected 0.82 | candidates 4/3/2 | 17.93 ms`

The candidate counts are:

`generated / valid / plausible`

If no candidate survives the civilization pipeline, the command reports:

`no civilization decision`

That is a valid result.

## Developer log

`logs/latest.log` now records:

- selected observer XYZ
- target XYZ
- final civilization score
- generated candidate count
- valid candidate count
- plausible candidate count
- visibility score
- landmark salience
- buildability
- terrain fit
- primary reason
- supporting reasons

## Civilization pipeline now live

`World Analysis`
→ `Observation Candidate Generation`
→ `Validity`
→ `Reason Evaluation`
→ `Scoring`
→ `Plausibility`
→ `Deterministic Selection`
→ `ObservationDecision`

This is the first live point where Wayfinder does more than interpret terrain.

It makes a civilization-level proposal:

> This location is a defensible place for the Wayfinders to observe this landmark.

## Test

Overlay this patch after Milestone 5.4 and run:

```powershell
.\gradlew.bat clean test
.\gradlew.bat build
.\gradlew.bat runClient
```

In Minecraft:

```text
/wayfinder analyze
```

Try:

- open plains
- rolling hills
- mountain valleys
- near large ridges

Expected behavior:

- unremarkable terrain may legitimately return no decision
- meaningful terrain may return an `OBSERVATION selected` result
- the selected observer and target coordinates in `logs/latest.log` should make
  sense when inspected in the world

## Next milestone

Once live decisions look believable, the next step is to introduce:

`ObservationDecision -> NodeProposal -> network validation`

Only after that should Wayfinder commit a persistent `OBSERVATION` Node.

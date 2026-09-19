# Wayfinder Milestone 4 — Live Visibility Integration

This patch wires the Milestone 3 sightline engine into the existing live `/wayfinder analyze` command.

## New live output

When a primary landmark exists, `/wayfinder analyze` now reports:

- how many observation candidates for the primary landmark have a validated sightline
- the best sightline score
- the best recognizability score

Example:

`Wayfinder: MOUNTAINOUS | high points 7 | landmarks 3 | observation sites 6 | primary PROMINENT_PEAK 0.86 | 2 visible | best 0.91 | recog 0.88 | 18.20 ms`

If no candidate passes the current visibility threshold, it reports:

`no validated sightline`

That is a valid result.

## Detailed developer log

For the best validated observation opportunity the log includes:

- observer XYZ
- landmark XYZ and type
- sightline score
- obstruction
- visible ray samples
- recognizability
- buildability
- original coarse observation-site score

## Small correctness fix

Milestone 2 can produce observation candidates for several landmarks. The visibility evaluator now filters sites to the selected target before ranking them, so a candidate created for a secondary landmark cannot accidentally be treated as a viewpoint for the primary landmark.

## Test

1. Overlay this ZIP on the same Wayfinder project.
2. Run:
   `.\gradlew.bat build`
3. Run:
   `.\gradlew.bat runClient`
4. Enter a world with visible hills/mountains.
5. Run:
   `/wayfinder analyze`
6. Try several locations.

The most valuable manual check is to compare the reported best observer/target coordinates in `logs/latest.log` against the actual terrain.

## Next gate

Once the live results look believable, the next milestone creates the first civilization-layer `OBSERVATION` candidate and applies:

`validity -> reason -> scoring -> plausibility -> deterministic selection`

No Watchtower blocks will be placed yet. The first goal is for Wayfinder to decide that a civilization had a defensible reason to observe a landmark from a particular site.

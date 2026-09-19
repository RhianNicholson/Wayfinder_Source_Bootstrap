# Wayfinder Milestone 2 — Semantic Geography v1

Overlay this patch onto the working Wayfinder project from Milestone 1.

## Adds
- local high-point detection
- landmark classification and salience scoring
- coarse observation-site opportunity scoring
- real slope/roughness derivation in terrain samples
- richer `/wayfinder analyze` output

## Test
```powershell
.\gradlew.bat build
.\gradlew.bat runClient
```

In a world:
```text
/wayfinder analyze
```

Expected summary now includes:
- terrain profile
- high-point count
- landmark count
- observation-site count
- primary landmark type + salience

The log also prints the primary landmark position and best observation opportunity.

Zero landmarks is valid in geographically unremarkable terrain. Try the command in hills or mountains as well as plains.

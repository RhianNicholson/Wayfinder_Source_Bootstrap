# Wayfinder Milestone 20 — Watchtower Real-Site Validation + Terrain Adaptation

Milestone 19 proved that Watchtower height can be derived from the landmark
sightline.

Milestone 20 makes that solved tower answer to the actual site.

## Rule

> The platform determines the tower. The terrain determines how the tower
> reaches the ground.

Validation is allowed to reject a tower.

It is not allowed to move the Observation Node, rotate the tower, change the
landmark target, or silently choose a taller platform.

## Validation

The first real-site gate checks:

- support foundation depth
- water beneath supports
- natural terrain occupying platform/view-frame cells
- the immediate target-facing view arc
- whether the already-solved platform height still satisfies the landmark
  sightline

Maximum additional support depth is 3 blocks.

If the real site would require a different platform height, the existing plan
is rejected rather than rewritten.

## Terrain adaptation

Accepted towers may extend individual support foundations downward to meet
uneven natural terrain.

Only the downward support/foundation geometry changes.

The following remain immutable:

- Observation Node position
- landmark target
- facing
- platform height
- platform geometry
- primary view arc

## Pipeline

```text
Observation history
       ↓
Watchtower intent
       ↓
sightline-solved geometry
       ↓
REAL-SITE VALIDATION
    ↙             ↘
 reject          accept
                   ↓
          support adaptation
                   ↓
          placement-ready plan
```

There is still no Watchtower world mutation in Milestone 20.

## Tests

Milestone 20 verifies:

1. flat terrain is accepted without changing platform height
2. a shallow support drop extends downward
3. a deep support drop rejects the tower
4. water beneath a support rejects the tower
5. terrain that would require a higher platform rejects the old plan instead
   of silently raising it

## Build

Overlay after Milestone 19.2:

```powershell
.\\gradlew.bat clean test
.\\gradlew.bat build
```

No `runClient` is required yet.

If this milestone passes, Milestone 21 can add the Watchtower Minecraft palette,
final placement guard integration, live preview, and guarded permanent placement.

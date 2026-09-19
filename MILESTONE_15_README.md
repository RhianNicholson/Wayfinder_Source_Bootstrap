# Wayfinder Milestone 15 — Structure Materialization Contract

This is the first physical-generation milestone.

No blocks are placed yet.

That is deliberate.

Wayfinder now has a hard architectural boundary:

```text
Committed Civilization Truth
          ↓
     Structure Intent
          ↓
 Structure Materialization Plan
          ↓
  [future Minecraft renderer]
          ↓
       Blocks
```

The civilization engine never asks Minecraft block placement to justify why a
structure exists.

## First mappings

```text
DIRECTION node
    + committed DIRECTIONAL_REFERENCE
    -> WAYSTONE_SHRINE

OBSERVATION node
    + geographic target
    -> WATCHTOWER
```

This implements the rule:

> Civilization decides why and where. Structure generation decides how.

## Empty space remains valid

A DIRECTION node alone does not materialize a Shrine.

The matching committed DIRECTIONAL_REFERENCE relationship must also exist.

If the historical evidence is incomplete, the planner emits no Shrine.

Nothing can still be the correct answer.

## Shrine intent

The Shrine plan preserves:

- historical source node
- exact origin
- target Observation node position
- eight-way architectural facing
- direction-communication purpose
- approach purpose
- glyph-surface purpose
- terrain anchoring purpose

Purposeful components:

```text
FOUNDATION
APPROACH
DIRECTION_AXIS
GLYPH_SURFACE
```

Every component has at least one explicit reason to exist.

## Watchtower intent

The Watchtower plan preserves:

- historical Observation node
- exact origin
- landmark anchor as semantic target
- primary view direction
- observation purpose
- protected view-arc purpose
- platform-support purpose
- terrain anchoring purpose

The component order deliberately reflects the locked design rule:

```text
OBSERVATION_PLATFORM
VIEW_ARC
TOWER_SUPPORT
FOUNDATION
```

The tower is conceptually solved from the observation platform downward.

## Minecraft independence

The new `com.wayfinder.structure` package imports no Minecraft classes.

It knows:

- civilization nodes
- relationships
- world positions
- structure semantics

It does not know:

- BlockState
- ServerLevel
- templates
- placement flags
- decorative palettes

That boundary is intentional and should remain strict.

## Build

Overlay after Milestone 14:

```powershell
.\gradlew.bat clean test
.\gradlew.bat build
```

Milestone 15 does not yet change `/wayfinder analyze`.

## Next step

With this contract validated, the next milestone can introduce a Minecraft-side
renderer/geometry solver.

That renderer must consume these plans rather than inventing structure purpose.

A likely next sequence is:

```text
Milestone 16
    Shrine geometry solver + dry-run block plan

Milestone 17
    Watchtower sightline/height geometry solver

Milestone 18
    guarded world placement
```

The exact split can adapt to what the build and tests reveal.

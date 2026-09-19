# Wayfinder Milestone 28 — WAY Glyph Physical Expression

Milestone 28 begins the transition from developer-visible systems to
player-readable world language.

The Waystone Shrine now physically expresses the first canonical Wayfinder
glyph:

```text
 ◇
```

Conceptually this is **WAY**.

## Physical pattern

In Shrine-local coordinates the glyph is:

```text
        (0,3,-1)

(-1,2,-1)     (1,2,-1)

        (0,1,-1)
```

All four cells use the existing `GLYPH_STONE` material role and
`GLYPH_SURFACE` function.

The result is a four-stone diamond rather than an arrow.

## Why it is not an arrow

The Wayfinder language communicates relationships, not object labels.

The glyph itself says roughly:

```text
WAY / relation / continuation
```

The Shrine's architecture supplies the directional axis.

The environment supplies what lies along that axis.

Together:

```text
glyph + orientation + structure + environment = meaning
```

This preserves the locked rule:

> The environment completes the clue.

## Existing worlds

Previously placed Shrines are historical materializations and are not rewritten.

To see the new glyph geometry physically, place a newly materialized Shrine in
a fresh test world or create a new eligible structure after applying M28.

This is intentional: a software update must not retroactively rewrite already
committed ancient structures.

## Test

Overlay after Milestone 27:

```powershell
.\gradlew.bat clean test
.\gradlew.bat build
.\gradlew.bat runClient
```

Use a fresh/new Shrine materialization and inspect its clue surface.

Expected:

- four chiseled glyph stones
- clear diamond arrangement
- glyph lies on the rear clue plane
- Shrine directional axis still points toward its semantic target
- glyph itself has no arrowhead

The next milestone should establish the Watchtower's **SIGHT** expression so
the player can encounter repeated language with a different contextual meaning.

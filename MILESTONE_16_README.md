# Wayfinder Milestone 16 — Waystone Shrine Geometry + Dry Run

Milestone 16 turns the abstract Shrine materialization contract into its first
deterministic block-level geometry plan.

It still does not place blocks.

That is important.

The new flow is:

```text
DIRECTION civilization truth
        ↓
WAYSTONE_SHRINE intent
        ↓
materialization contract
        ↓
WaystoneShrineGeometrySolver
        ↓
dry-run block cells
        ↓
[future guarded Minecraft renderer]
```

## Architectural clue first

The Shrine's direction is expressed by architecture, not only by a glyph.

Local layout:

```text
          toward Observation
                 ↑

             terminal
                ■
                │
        pier    │    pier
         █      │      █
         █     glyph   █
         █      │      █
        ───── foundation ─────
                │
                │
             approach
                │
             approach
                │
             approach
```

The exact masonry is intentionally still simple. This milestone proves the
geometry contract and orientation mechanics before palette work or ruin
variation.

## Purposeful block roles

Every emitted dry-run cell has both:

- a semantic material role
- a functional reason

Material roles:

```text
FOUNDATION_STONE
PRIMARY_STONE
ACCENT_STONE
GLYPH_STONE
```

Functions:

```text
FOUNDATION
APPROACH
DIRECTION_AXIS
SHRINE_BODY
GLYPH_SURFACE
```

There is no generic decorative/filler function.

## Eight-way orientation

The Shrine is solved in local coordinates and transformed into world
coordinates for all eight semantic facings:

```text
N
NE
E
SE
S
SW
W
NW
```

Diagonal Shrines stay genuinely diagonal rather than being silently rounded to
a cardinal orientation.

## Damage-resilient meaning

The directional axis contains more physical evidence than the glyph panel.

That encodes the established rule:

> The Shrine's architectural axis is part of the clue.

If the glyph surface is later damaged by historical simulation, the structure
can still communicate that it was intentionally oriented toward something.

## Dry-run only

`StructureGeometryPlan` is still Minecraft-independent.

It contains world coordinates, semantic material roles, and block functions,
but knows nothing about:

- `BlockState`
- palette registries
- placement flags
- chunk writes
- structure templates

This allows geometry to be tested independently from world mutation.

## Tests

Milestone 16 verifies:

1. Shrine axis points toward its semantic target
2. approach lies behind the directional axis
3. diagonal orientation is preserved
4. glyph is reinforcement, not the sole directional clue
5. no two planned block cells overlap
6. every cell has a semantic function
7. dry-run summary exposes functional composition

## Build

Overlay after Milestone 15:

```powershell
.\gradlew.bat clean test
.\gradlew.bat build
```

There is intentionally no `runClient` requirement for this milestone because
nothing mutates or renders in Minecraft yet.

## Next physical step

Once this passes, the clean next milestone is terrain adaptation and guarded
previewing: validate that the solved Shrine can fit the real site, then expose
the dry-run plan in-game without committing permanent blocks.

After that, actual placement can be enabled behind a strict validation gate.

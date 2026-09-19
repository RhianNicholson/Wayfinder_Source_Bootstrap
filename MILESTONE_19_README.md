# Wayfinder Milestone 19 — Watchtower Geometry + Sightline Height Solver

The first physical Shrine now exists.

Milestone 19 begins the second foundational structure: the Watchtower.

It deliberately returns to dry-run geometry before any Watchtower blocks are
allowed into Minecraft.

## Governing rule

> The Watchtower is designed from the observation platform downward.

The tower does not receive a random or decorative height.

Its height is solved from its historical job:

```text
OBSERVATION node
      ↓
Landmark target
      ↓
minimum usable sightline
      ↓
platform height
      ↓
supports
      ↓
foundation
```

## Sightline solver

The first-slice solver searches tower heights:

```text
minimum: 6 blocks
maximum: 24 blocks
required visibility score: 0.75
```

It selects the **lowest height that works**.

This means a flat/open site gets a modest tower.

An intervening ridge can justify a taller tower.

If even the maximum height cannot establish the required sightline, no tower
geometry is produced.

The generator is not allowed to say "a tower belongs here" and then build one
that fails its purpose.

## Platform geometry

The first platform is 5x5.

A low view frame protects three sides, while the center of the target-facing
edge remains open.

That opening is structural evidence:

```text
              LANDMARK
                 ↑
          primary view arc

          ┌────     ────┐
          │             │
          │   platform  │
          │             │
          └─────────────┘
             supports
                ↓
             terrain
```

The platform should communicate observation even without a glyph.

## Downward construction

Only after the platform height is solved are four corner supports generated
downward.

So the code literally follows the design principle rather than merely
documenting it.

## New semantic block roles

Milestone 19 extends the block-plan vocabulary with:

```text
TOWER_FOUNDATION_STONE
TOWER_SUPPORT_STONE
PLATFORM_STONE
VIEW_ACCENT_STONE
```

and functional roles:

```text
TOWER_FOUNDATION
TOWER_SUPPORT
OBSERVATION_PLATFORM
VIEW_FRAME
```

These remain Minecraft-independent.

## Tests

Milestone 19 verifies:

1. flat terrain uses the minimum functional tower height
2. an intervening ridge forces a taller platform
3. an impossible sightline creates no tower
4. the primary target-facing view cell remains open
5. supports are generated downward from the solved platform

## Build

Overlay after Milestone 18:

```powershell
.\gradlew.bat clean test
.\gradlew.bat build
```

No `runClient` requirement yet.

Milestone 20 can then apply real-site Watchtower validation and terrain
adaptation, followed by guarded Watchtower placement once that physical contract
has proven itself.

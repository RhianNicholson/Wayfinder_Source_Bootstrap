# Wayfinder Milestone 17 — Shrine Site Validation + Terrain Adaptation

Milestone 17 makes the first Shrine geometry answer to the actual Minecraft
terrain.

The governing rule is:

> The Wayfinders adapt to the world. The world does not rearrange itself to
> accommodate the Wayfinders.

But adaptation has a hard boundary:

> Terrain may change the foundation. Terrain may not rewrite history.

The Shrine's:

- committed node position
- architectural facing
- semantic target
- approach direction
- directional axis
- glyph surface

remain immutable.

## Site validation

A solved Shrine is checked against the real `WorldTerrainView`.

The first validation rules are:

```text
maximum terrain spread:          3 blocks
maximum rise above Shrine base:  1 block
maximum foundation drop:         3 blocks
water in footprint:              rejected
occupied upper structure space:  rejected
```

These are deliberately conservative first-slice thresholds.

A failed site does not cause the structure to move to a more convenient
coordinate.

It simply does not materialize.

## Foundation adaptation

If the site is valid but terrain falls modestly beneath parts of the Shrine,
the foundation extends downward until it meets the local surface.

Only foundation masonry is added.

Example:

```text
planned Shrine base
████████████
████████████
   ███
   ███       <- adaptive foundation support
___███____   <- natural terrain
```

Upper structure geometry is untouched.

## New validation gate

```text
Civilization truth
      ↓
Shrine intent
      ↓
Shrine materialization contract
      ↓
Shrine geometry
      ↓
SITE VALIDATION
   ↙       ↘
reject    accept
            ↓
    foundation adaptation
            ↓
       renderable plan
```

There is still no permanent world placement.

## In-game dry preview

Milestone 17 adds:

```text
/wayfinder shrinepreview
```

The command chooses the nearest committed Shrine intent and evaluates it against
the real terrain.

Accepted example:

```text
Wayfinder Shrine preview: ACCEPTED
origin [...]
facing EAST
blocks 44
foundation +6
terrain spread 2
NO WORLD CHANGES
```

Rejected example:

```text
Wayfinder Shrine preview: REJECTED
terrain spread 6
issues TERRAIN_TOO_UNEVEN,FOUNDATION_DROP_TOO_DEEP
```

The preview is strictly read-only:

- no blocks
- no entities
- no particles
- no SavedData mutation

## Tests

Milestone 17 verifies:

1. flat dry terrain is accepted
2. shallow terrain drops extend foundation
3. adaptation never changes historical origin
4. adaptation never changes facing
5. steep terrain is rejected instead of moving the Shrine
6. water rejects the site
7. rejected sites do not produce adapted geometry

## Build and live validation

Overlay after Milestone 16:

```powershell
.\gradlew.bat clean test
.\gradlew.bat build
.\gradlew.bat runClient
```

Then, in a world where the Direction -> Observation chain already exists:

```text
/wayfinder shrinepreview
```

This is the final safety gate before actual Shrine block placement.

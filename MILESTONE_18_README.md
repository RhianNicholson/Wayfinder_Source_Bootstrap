# Wayfinder Milestone 18 — First Permanent Waystone Shrine

Milestone 18 crosses the physical boundary.

For the first time, Wayfinder is permitted to place civilization-derived
blocks into Minecraft.

It is deliberately manual and guarded.

## Command

```text
/wayfinder shrineplace
```

The command reruns the full causal chain before mutation:

```text
committed civilization truth
        ↓
Shrine intent
        ↓
materialization contract
        ↓
geometry solver
        ↓
real-site validation
        ↓
foundation adaptation
        ↓
placement guard
        ↓
Minecraft block palette
        ↓
WORLD MUTATION
```

## First palette

```text
FOUNDATION_STONE -> COBBLESTONE
PRIMARY_STONE    -> STONE_BRICKS
ACCENT_STONE     -> POLISHED_ANDESITE
GLYPH_STONE      -> CHISELED_STONE_BRICKS
```

The structure layer still has no Minecraft block dependency. Palette mapping
exists only in `com.wayfinder.minecraft.structure`.

## Final placement states

```text
READY
ALREADY_PRESENT
PARTIAL_EXISTING_STRUCTURE
BLOCKED
```

`READY` permits placement.

`ALREADY_PRESENT` changes zero blocks, so an immediate rerun is idempotent.

`PARTIAL_EXISTING_STRUCTURE` is refused. Wayfinder does not infer that missing
ancient blocks should be restored.

`BLOCKED` is refused with no world changes.

This encodes:

> Damage may remove information, but generation may not invent a repair.

## Build and live test

Overlay after Milestone 17:

```powershell
.\gradlew.bat clean test
.\gradlew.bat build
.\gradlew.bat runClient
```

In the existing validation world:

```text
/wayfinder shrinepreview
```

Confirm the target Shrine reports `ACCEPTED`.

Then run:

```text
/wayfinder shrineplace
```

Expected first result:

```text
Wayfinder Shrine placement: PLACED
```

Run the exact same command again immediately.

Expected:

```text
Wayfinder Shrine placement: ALREADY PRESENT
```

with zero changed blocks.

Optional historical-damage test: remove one upper Shrine block and run
`/wayfinder shrineplace` again. Expected:

```text
REFUSED PARTIAL STRUCTURE
NO REPAIR PERFORMED
```

## Why this milestone matters

The running chain is now:

```text
GEOGRAPHY
   ↓
CIVILIZATION REASON
   ↓
COMMITTED NODE
   ↓
COMMITTED RELATIONSHIP
   ↓
STRUCTURE INTENT
   ↓
PURPOSEFUL GEOMETRY
   ↓
TERRAIN ADAPTATION
   ↓
PHYSICAL MINECRAFT STRUCTURE
```

The Shrine exists because the simulated Wayfinders had a reason to build it,
not because a structure generator independently decided to scatter a Shrine.

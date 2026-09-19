# Wayfinder Milestone 21 — Live Watchtower Preview + Guarded Placement

Milestone 21 crosses the same boundary the Shrine crossed in Milestone 18:
functional structure geometry may now become real Minecraft blocks.

## Commands

```text
/wayfinder towerpreview
/wayfinder towerplace
```

`towerpreview` is read-only.

It reports:

- committed Observation node
- origin
- facing
- solved platform Y
- solved tower height
- visibility score
- total adapted block count
- downward support extension

`towerplace` repeats all functional and site gates before mutation.

## Minecraft palette

Watchtower semantic roles map to:

```text
TOWER_FOUNDATION_STONE -> Cobblestone
TOWER_SUPPORT_STONE    -> Stone Bricks
PLATFORM_STONE         -> Smooth Stone
VIEW_ACCENT_STONE      -> Polished Andesite
```

The Watchtower palette rejects Shrine roles.

The Shrine palette already rejects Watchtower roles.

This keeps renderer ownership explicit.

## Placement guard

Milestone 21 also generalizes the historical-damage distinction in
`StructurePlacementGuard`.

Both:

```text
FOUNDATION
TOWER_FOUNDATION
```

are foundation cells.

A naturally matching foundation block is therefore not sufficient evidence that
a historical upper structure already exists.

Matching upper tower cells still trigger:

```text
REFUSED PARTIAL STRUCTURE
```

rather than automatic repair.

## Live placement pipeline

```text
committed OBSERVATION node
       ↓
WATCHTOWER intent
       ↓
sightline height solve
       ↓
real-site validation
       ↓
support adaptation
       ↓
placement guard
       ↓
Minecraft block mutation
```

No civilization state is changed by placement.

## Test procedure

After overlaying Milestone 21:

```powershell
.\gradlew.bat clean test
.\gradlew.bat build
.\gradlew.bat runClient
```

In the same world used for the Shrine:

```text
/wayfinder towerpreview
```

Expected on a valid site:

```text
Wayfinder Watchtower preview: ACCEPTED ...
```

Then:

```text
/wayfinder towerplace
```

Expected:

```text
Wayfinder Watchtower placement: PLACED ...
```

Inspect the tower physically.

The platform should face the landmark and its target-facing center should remain
visually open.

Do not expect a decorative fantasy tower yet. This is the first functional
physical contract.

## Historical-damage check

Optionally remove one upper platform/support block and run:

```text
/wayfinder towerplace
```

The generator must refuse automatic repair rather than rewriting historical
damage.

## Next milestone

Once Watchtower placement is validated, the next architectural step should be
persistent materialization truth.

That will let Wayfinder distinguish reliably across restarts between:

- never materialized
- physically intact
- partially damaged
- completely lost

without reconstructing ownership from terrain that the structure itself has
already modified.

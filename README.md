# Wayfinder

Wayfinder is an environmental archaeology and navigation mod for Minecraft. Structures, landscape, glyphs and historical state form a civilization network that the player learns to interpret without a quest log.

## Development target

- Minecraft 26.2
- NeoForge 26.2.0.62
- Java 25
- ModDevGradle 2.0.147

## Current developer command

After launching a development world, run:

```text
/wayfinder analyze
```

The command samples a 256x256 area centered on the player at 8-block spacing, classifies the terrain, and reports sample count, elevation range, terrain profile and analysis time. Detailed metrics are written to the development log.

The command is intentionally observational: it does not modify the Minecraft world.

## Architecture rule

Minecraft is an adapter to the Wayfinder engine, not the foundation of the engine. Loader-neutral domain and geography packages must not depend on Minecraft classes.

# Milestone 19.1 — Palette Exhaustiveness Fix

Milestone 19 added Watchtower-specific values to `BlockMaterialRole`, so Java
correctly required the existing Shrine palette switch to handle those values.

This patch preserves the architectural boundary:

- Shrine roles map to Shrine blocks.
- Watchtower roles are explicitly rejected by the Shrine palette.
- No fake/default Watchtower mapping is introduced merely to satisfy the compiler.

Apply after Milestone 19, then run:

```powershell
.\gradlew.bat clean test
.\gradlew.bat build
```

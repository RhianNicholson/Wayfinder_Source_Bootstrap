# Milestone 28.1 — Approved Waystone Shrine Design

This replaces the earlier M28 visual attempt with the approved design.

The Shrine now has a centered stepped sanctuary wall, twin tall flanking
pillars, an open target-facing side, and the existing directional axis.

The WAY glyph is exactly four chiseled blocks arranged around an open center.
The glyph blocks replace wall cells rather than floating as disconnected
ornamentation. There is no arrowhead or decorative side projection.

The glyph communicates WAY/continuation. The architecture communicates
direction. The environment completes the clue.

Test with a newly materialized Shrine in a fresh world:

```powershell
.\gradlew.bat clean test
.\gradlew.bat build
.\gradlew.bat runClient
```

Confirm: four-block diamond, open center, embedded rear-wall glyph, twin
pillars, open front, intact directional axis, and no long arrow-like extension.
Existing persisted Shrines are intentionally not rewritten.

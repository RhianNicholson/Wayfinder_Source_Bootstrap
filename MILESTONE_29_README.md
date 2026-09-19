# Milestone 29 — SIGHT Glyph Physical Expression

Milestone 28.1 locked the approved Waystone Shrine and its ◇ WAY language.

Milestone 29 gives the Watchtower its complementary player-visible symbol:

```text
△ SIGHT
```

The triangle is expressed on the rear observation frame, opposite the
target-facing opening.

This placement is deliberate. The glyph never occupies the primary view arc.

The player can therefore encounter:

```text
Shrine:     ◇ + directional architecture + environment
Watchtower: △ + observation platform + open view + landmark
```

The symbols are not object labels. Context supplies their meaning.

## Test

Overlay after Milestone 28.1:

```powershell
.\gradlew.bat clean test
.\gradlew.bat build
.\gradlew.bat runClient
```

Use a newly materialized Watchtower.

Confirm:

- the platform still solves from sightline requirements;
- the target-facing center remains open;
- the rear frame carries a clear three-point triangular expression;
- the glyph does not block the landmark view;
- supports still descend from the platform rather than determining its height.

Existing persisted Watchtowers are intentionally not rewritten.

Once this passes, WAY and SIGHT will both exist as physical language in the
world, allowing the next phase to test the player's complete visual discovery
sequence rather than individual structures in isolation.

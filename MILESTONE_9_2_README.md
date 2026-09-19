# Wayfinder Milestone 9.2 — Test Runtime Decoupling Fix

## Cause

The Milestone 9 persistence DTOs contained static Mojang `Codec` fields.

That meant loading a plain civilization persistence class during JUnit also
forced the JVM to load Mojang serialization classes. The normal domain test
runtime does not provide those Minecraft runtime classes, producing
`NoClassDefFoundError`.

The round-trip persistence logic itself was not the failing behavior.

## Architectural fix

Persistence is now divided cleanly:

```text
Civilization domain
        ↓
plain persistence DTOs
        ↓
CivilizationStatePersistenceMapper
        ↓
Minecraft persistence adapter
        ↓
Mojang Codec / SavedData
```

The following classes are now plain Java:

- `PersistedPosition`
- `PersistedLandmark`
- `PersistedReason`
- `PersistedReasonEvaluation`
- `PersistedCivilizationNode`

All Mojang `Codec` definitions now live exclusively in:

```text
WayfinderCivilizationSavedData
```

This improves the architecture as well as fixing the test runtime problem:
the civilization layer no longer depends on Minecraft serialization merely
because it needs a persistence representation.

## Run

Overlay this patch after Milestones 9 and 9.1:

```powershell
.\gradlew.bat clean test
.\gradlew.bat build
.\gradlew.bat runClient
```

If tests/build pass, continue with the restart persistence test from Milestone 9.

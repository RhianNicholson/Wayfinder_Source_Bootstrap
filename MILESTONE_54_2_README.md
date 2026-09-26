# Milestone 54.2 — Live Era Advance Command

Built from the exact uploaded current `WayfinderCommands.java`.

Adds:

```text
/wayfinder advanceera
```

The command:
1. derives the semantic `HistoricalRegionKey` from command-source position;
2. loads persisted regional era state;
3. evaluates an explicit one-era transition;
4. commits it through `CivilizationEraLifecycleService`;
5. saves through `MinecraftSavedDataCivilizationEraRepository`;
6. reports region and before/after era.

This preserves every command already present in the uploaded command tree.

Important: `/wayfinder generatehistory` remains registered through
`HistoricalGenerationCommand`. M54.3 will update that exact class to use
`MinecraftHistoricalScopeResolver`; it should not be guessed from an older
snapshot.

Validation:

```powershell
.\gradlew.bat clean test
.\gradlew.bat build
.\gradlew.bat runClient
```

Then:

```text
/wayfinder advanceera
```

Run it twice in the same region and confirm `1 -> 2`, then `2 -> 3`.

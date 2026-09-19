# Milestone 37.1 — Repository Adapter Fix

M37 assumed domain repository interfaces existed for all three persistent
stores. The working project actually uses concrete Minecraft SavedData
repositories for materialization and historical events.

This patch aligns the orchestrator with the validated adapters:

- `MinecraftSavedDataCivilizationRepository`
- `MinecraftSavedDataMaterializationRepository`
- `MinecraftSavedDataHistoricalEventRepository`

It also corrects `MaterializationCommitService.updateCondition(...)` to the
existing signature `(state, record, condition)`.

No historical-domain behavior changes.

Run:

```powershell
.\gradlew.bat clean test
.\gradlew.bat build
```

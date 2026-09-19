# Milestone 35.1 — Result Accessor Fix

Fixes two incorrect accessor names in `HistoricalGenerationService`.

- `RouteLossTransitionService.Validation.accepted()` -> `valid()`
- `HistoricalEventCommitService.Result.accepted()` -> `committed()`

No behavior or architecture changes.

Run:

```powershell
.\gradlew.bat clean test
.\gradlew.bat build
```

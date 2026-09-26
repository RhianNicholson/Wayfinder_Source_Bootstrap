# Milestone 54.1 — HistoricalEraKey Type Fix

M54 passed a String (`CivilizationEra.key()`) into `HistoricalScope`, but the
actual scope API requires the strongly typed `HistoricalEraKey`.

The resolver now constructs:

```java
new HistoricalEraKey(era.value())
```

This keeps persisted civilization era state numeric/domain-owned while
`HistoricalScope` receives the exact historical-era type expected by the
existing engine.

Apply over M54:

```powershell
.\gradlew.bat clean test
.\gradlew.bat build
```

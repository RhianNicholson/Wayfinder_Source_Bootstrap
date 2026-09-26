# Milestone 54.3 — Persisted Era Generate-History Wiring

Built from the exact uploaded current `HistoricalGenerationCommand.java`.

The command previously contained:

```java
private static final int CURRENT_ERA = 1;
```

and constructed `HistoricalScope` directly from that constant.

M54.3 removes the hardcoded era and routes live scope creation through:

```java
new MinecraftHistoricalScopeResolver().resolve(level, position)
```

The live path is now:

```text
/wayfinder advanceera
        ↓
persist CivilizationEraState
        ↓
/wayfinder generatehistory
        ↓
MinecraftHistoricalScopeResolver
        ↓
load persisted era for current HistoricalRegionKey
        ↓
HistoricalScope
        ↓
generalized history engine
```

Validation:

```powershell
.\gradlew.bat clean test
.\gradlew.bat build
.\gradlew.bat runClient
```

In one region:

```text
/wayfinder advanceera
/wayfinder generatehistory
```

The `generatehistory` output should report the newly persisted era. Advance
again and verify the next `generatehistory` invocation reports the next era.

This completes removal of the live hardcoded Era 1 assumption.

# Milestone 43.1 — RuinRemnantPolicy Import Fix

M43 referenced `RuinRemnantPolicy` from the Minecraft history package without
importing its actual domain package.

Actual type:

```java
com.wayfinder.history.RuinRemnantPolicy
```

This patch adds that import only. No behavior or M43 semantics are changed.

Apply after M43 and run:

```powershell
.\gradlew.bat clean test
.\gradlew.bat build
```

If those pass, runClient and validate `/wayfinder generatehistory` as described
in M43.

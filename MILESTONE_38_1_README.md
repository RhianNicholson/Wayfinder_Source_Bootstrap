# Milestone 38.1 — Command Registration Fix

The uploaded `WayfinderCommands.java` confirmed that `generatehistory` was not
registered under the `/wayfinder` command tree.

This patch updates that exact file and adds:

```java
.then(Commands.literal("generatehistory")
        .executes(HistoricalGenerationCommand::execute))
```

Because `HistoricalGenerationCommand` is in the same package, no import is
required.

Overlay this patch after M38, then run:

```powershell
.\gradlew.bat clean test
.\gradlew.bat build
.\gradlew.bat runClient
```

In-game, verify autocomplete and run:

```text
/wayfinder generatehistory
```

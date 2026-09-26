# Milestone 45.1 — NodeId Import Fix

The project source confirms `NodeId` lives at:

```java
com.wayfinder.core.id.NodeId
```

M45 incorrectly imported it from `com.wayfinder.civilization.model`.

This patch corrects that import only. No historical-opportunity behavior changes.

Apply after M45:

```powershell
.\gradlew.bat clean test
.\gradlew.bat build
```

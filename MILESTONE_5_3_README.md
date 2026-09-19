# Wayfinder Milestone 5.3 — UTF-8 BOM Fix

The previous PowerShell patch used `Set-Content -Encoding UTF8`.

On Windows PowerShell 5.x, that writes a UTF-8 BOM. Gradle then encountered the BOM before `plugins {` and reported:

`Unexpected character: '∩╗┐'`

## Fix

This patch:

- strips an existing UTF-8 BOM from `build.gradle`
- preserves/adds the JUnit Platform launcher dependency
- writes `build.gradle` back as UTF-8 **without BOM**

## Apply

Overlay this ZIP into the project root, then run:

```powershell
powershell -ExecutionPolicy Bypass -File .\tools\fix-build-gradle-encoding.ps1
```

Then:

```powershell
.\gradlew.bat clean test
.\gradlew.bat build
```

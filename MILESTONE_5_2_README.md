# Wayfinder Milestone 5.2 — JUnit Runtime Fix

The mod source compiled successfully, but Gradle could not start the JUnit Platform test executor.

## Cause

The test runtime was missing the JUnit Platform launcher.

With modern Gradle/JUnit setups, the launcher should be explicitly available on the test runtime classpath:

```groovy
testRuntimeOnly 'org.junit.platform:junit-platform-launcher'
```

This does not affect Minecraft runtime code. It only fixes the Gradle test process.

## Apply

1. Overlay this ZIP into the project root.
2. From PowerShell in the Wayfinder project root, run:

```powershell
.\tools\apply-junit-runtime-fix.ps1
```

If PowerShell blocks local scripts, run:

```powershell
powershell -ExecutionPolicy Bypass -File .\tools\apply-junit-runtime-fix.ps1
```

3. Then run:

```powershell
.\gradlew.bat clean test
.\gradlew.bat build
```

The script is idempotent; if the launcher dependency is already present it makes no change.

# Wayfinder Milestone 7.1 — Record Accessor Compile Fix

## Cause

`NetworkValidationResult` is a Java record with the component:

```java
boolean accepted
```

Java automatically generates the accessor:

```java
boolean accepted()
```

Milestone 7 also defined a static factory named:

```java
NetworkValidationResult accepted()
```

Java does not allow that method to collide with the generated record accessor.

## Fix

The static factories are now:

```java
NetworkValidationResult.allow()
NetworkValidationResult.reject(...)
```

The generated record accessor remains:

```java
validation.accepted()
```

So `NodeAdmissionDecision.accepted()` continues to work exactly as intended.

## Run

Overlay this patch after Milestone 7, then run:

```powershell
.\gradlew.bat clean test
.\gradlew.bat build
```

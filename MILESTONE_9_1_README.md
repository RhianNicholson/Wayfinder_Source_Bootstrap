# Wayfinder Milestone 9.1 — Lambda Capture Compile Fix

## Cause

Milestone 9 loads `civilizationState` and may later reassign it after a
successful commit.

Java therefore does not consider `civilizationState` effectively final.

The `sendSuccess` message supplier is a lambda, and the lambda attempted to
capture:

```java
civilizationState.nodes().size()
```

That is illegal for a reassigned local variable.

## Fix

The final value needed by the message is calculated before constructing the
lambda:

```java
int committedNodeCount = civilizationState.nodes().size();
```

The lambda captures only `committedNodeCount`, which is effectively final.

No persistence or civilization behavior has changed.

## Run

Overlay this patch after Milestone 9:

```powershell
.\gradlew.bat clean test
.\gradlew.bat build
.\gradlew.bat runClient
```

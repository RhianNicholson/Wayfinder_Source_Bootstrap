# Wayfinder Milestone 13.1 — Command Typed-Target Compile Fix

## Cause

Milestone 13 changed:

```java
CivilizationNode.target()
```

from a concrete `Landmark` to the new `NodeTarget` interface.

`WayfinderCommands` still had one debug logging block that called:

```java
node.target().anchor()
```

That is no longer legal because a Direction node can target another
civilization node rather than geography.

## Fix

The existing debug coordinates now safely use the geographic target when
present:

```java
node.geographicTarget()
    .map(target -> target.anchor().x())
    .orElse(node.position().x())
```

The same change is applied for Y and Z.

For existing Observation nodes, output is unchanged.

For future Direction nodes, the log falls back to the Direction node's own
position until a dedicated typed-target log format is added.

No domain, persistence, validation, or selection behavior changes.

## Run

Overlay after Milestone 13:

```powershell
.\gradlew.bat clean test
.\gradlew.bat build
```

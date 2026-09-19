# Wayfinder Milestone 13.2 — Typed-Target Test Fixture Fix

Milestone 13 correctly made this invalid:

```text
DIRECTION Node -> Landmark
```

Six older Milestone 10/11 relationship tests still created their synthetic
DIRECTION nodes that way, so the new domain invariant rejected the fixtures
before the relationship behavior under test could run.

This patch updates only those test fixtures.

They now construct:

```text
DIRECTION   -> CivilizationNodeTarget(Observation NodeId)
OBSERVATION -> GeographicNodeTarget(Landmark)
```

The production invariant is not relaxed.

That is important: a failing old test fixture should adapt to the improved
domain model; the model should not be weakened to preserve an obsolete test
shortcut.

Run:

```powershell
.\gradlew.bat clean test
.\gradlew.bat build
```

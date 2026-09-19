# Wayfinder Milestone 26 — Broken Continuation Selection

Milestone 25 proved that a recorded structure can be lost safely.

Milestone 26 changes the question from "what nearby structure can we destroy?"
to "which committed relationship represents a meaningful continuation that
history is allowed to break?"

The selector requires an existing committed:

```text
DIRECTION -> DIRECTIONAL_REFERENCE -> OBSERVATION
```

and the Observation node must have a real, non-LOST materialization record.

No destination is invented for the sake of creating drama. Civilization truth
creates the possibility; history may later alter it.

## Test

Overlay after Milestone 25:

```powershell
.\gradlew.bat clean test
.\gradlew.bat build
```

No runClient test is required.

## Next

Milestone 27 will wire this selector to the already-tested route-loss transition.
The Shrine will retain its committed directional relationship while the
Watchtower it once led toward becomes physically absent.

That creates the intended contradiction: the player read the clue correctly,
but history changed.

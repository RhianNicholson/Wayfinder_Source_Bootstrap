# Wayfinder Milestone 27 — First Broken Discovery Chain

New developer command:

```text
/wayfinder breakcontinuation
```

It requires committed civilization truth:

```text
DIRECTION -> DIRECTIONAL_REFERENCE -> OBSERVATION -> LANDMARK
```

The Observation node must have a real non-LOST materialization.

The command commits and persists ROUTE_LOSS before removing the surviving
matching Watchtower blocks. It then marks the Watchtower materialization LOST.

The Direction node, Directional Reference, Observation node, landmark target,
and original materialization geometry remain historical truth.

The result is the first executable Wayfinder contradiction:

> The Shrine still points somewhere meaningful. The destination is gone.

## Test

```powershell
.\gradlew.bat clean test
.\gradlew.bat build
.\gradlew.bat runClient
```

Use a world with a Milestone 23+ recorded Shrine and Watchtower that are joined
by the committed directional relationship.

Run:

```text
/wayfinder breakcontinuation
```

Expected output includes `APPLIED`, `DIRECTIONAL_REFERENCE`, and
`relationship preserved`.

Then inspect the lost Watchtower with:

```text
/wayfinder materializationstatus
```

Its persistent record should report LOST.

The next phase can move from developer-visible history to player-discoverable
clue-language and physical evidence.

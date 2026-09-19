# Milestone 30 — First Discovery Sequence Validation

M28.1 and M29 established `◇ WAY` and `△ SIGHT`. M30 stops adding vocabulary
and evaluates whether committed world truth contains a coherent first discovery
sequence.

It recognizes either:

```text
Shrine/WAY -> committed relationship -> Watchtower/SIGHT -> Landmark
```

or the historical contradiction:

```text
Shrine/WAY -> committed relationship -> LOST destination
```

This creates no quest, waypoint, tutorial, or player knowledge. It only checks
whether the world contains the evidence needed for discovery.

Test:

```powershell
.\gradlew.bat clean test
.\gradlew.bat build
```

No runClient test is required. Once this validates, we can expose a
developer-only diagnostic and conduct the first end-to-end player-facing
discovery test without using that diagnostic during play.

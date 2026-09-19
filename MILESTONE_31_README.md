# Milestone 31 — Discovery Status Diagnostic

Adds developer instrumentation:

```text
/wayfinder discoverystatus
```

It reports whether persistent world truth contains a coherent first discovery
sequence. It is not player-facing UI.

Test:

```powershell
.\gradlew.bat clean test
.\gradlew.bat build
.\gradlew.bat runClient
```

For an intact chain, `discoverystatus` should report `COHERENT` with WAY shrine,
directional relationship, SIGHT tower, and landmark evidence.

After `/wayfinder breakcontinuation`, it should remain `COHERENT` and include
`BROKEN_CONTINUATION_PRESENT`.

After verifying the diagnostic, do the actual discovery test without debug
commands: begin at the Shrine, interpret its WAY glyph and architecture, find
the Watchtower, interpret SIGHT and its open view, then identify the privileged
landmark. Repeat with a broken continuation.

The test is about inference from the world, not remembering our internal names.

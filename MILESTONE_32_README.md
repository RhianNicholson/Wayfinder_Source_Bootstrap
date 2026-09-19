# Milestone 32 — Historical Ruin States

Route loss no longer has to mean visually perfect erasure.

For a lost Watchtower, the physical loss applier now preserves only cells that
were genuinely part of the original committed structure:

- recorded foundation cells;
- the lowest two recorded courses of tower supports.

Everything informational is removed:

- observation platform;
- view frame;
- SIGHT glyph expression;
- upper supports.

No rubble is invented. No new blocks are placed. Player-modified blocks remain
protected by the existing exact-match guard.

This implements the rule:

> Damage may remove information, but may not invent information.

The persistent materialization condition still becomes `LOST`. `LOST` means the
original functional structure no longer exists; it does not require every
historical stone to have vanished.

## Test

Overlay after Milestone 31:

```powershell
.\gradlew.bat clean test
.\gradlew.bat build
.\gradlew.bat runClient
```

Create a fresh Shrine -> Watchtower chain and inspect the intact Watchtower.
Then run:

```text
/wayfinder breakcontinuation
```

Expected physical result:

- platform disappears;
- SIGHT/view frame disappears;
- upper tower disappears;
- low support stubs and foundations remain;
- the Shrine and directional relationship remain;
- `/wayfinder discoverystatus` remains `COHERENT` and includes
  `BROKEN_CONTINUATION_PRESENT`.

The remnant should communicate only: "something constructed was here."
It should not explain what happened or conveniently preserve the clue.

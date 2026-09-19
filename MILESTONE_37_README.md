# Milestone 37 — Live Historical Generation Command Boundary

This milestone introduces the Minecraft-side orchestration class for one
historical-generation pass.

The orchestrator performs:

```text
load persistent truth
 -> discover plausible route-loss candidates
 -> deterministic seed/region/era decision
 -> validate + commit event
 -> save history FIRST
 -> recover/apply pending physical consequence
 -> inspect actual remaining blocks
 -> persist resulting materialization condition
```

Unlike the earlier debug break command, the event itself is selected by the
historical engine.

The physical state is inspected after M32's ruin transformation, so surviving
foundation/support remnants naturally result in DAMAGED rather than falsely
claiming complete physical absence.

The orchestration class is intentionally separate from command registration.
That keeps command syntax/UI out of the historical engine and makes this same
operation reusable later from automatic region/era generation hooks.

## Important

This patch establishes the live orchestration boundary. Because the project's
existing repository constructors/command registration are Minecraft adapter
details that have changed across earlier milestones, compile validation is the
next checkpoint before adding the small `/wayfinder generatehistory` command
adapter.

## Test

Overlay after M36:

```powershell
.\gradlew.bat clean test
.\gradlew.bat build
```

If either repository interface or Minecraft adapter package differs in the
working tree, send the compiler output; the correction will be isolated to this
adapter rather than changing the historical domain.

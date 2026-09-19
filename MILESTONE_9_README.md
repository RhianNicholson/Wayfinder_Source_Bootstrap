# Wayfinder Milestone 9 — Persistent Civilization State

Milestone 9 replaces the temporary static in-memory civilization state with
Minecraft-managed global `SavedData`.

## Architectural boundary

The civilization engine still knows only:

```text
CivilizationStateRepository
```

Minecraft-specific storage lives behind:

```text
MinecraftSavedDataCivilizationRepository
WayfinderCivilizationSavedData
```

The domain layer never asks Minecraft how data is written.

## Stored truth

Milestone 9 persists committed node truth:

- NodeId
- NodePurpose
- node position
- target Landmark identity and semantic attributes
- final admitted score
- primary reason
- supporting reasons
- source proposal key

It intentionally does **not** persist:

- terrain sample grids
- visibility rays
- candidate lists
- temporary scores
- command analysis results

Those are reconstructable evidence, not committed civilization history.

## Save location

The data uses Minecraft/NeoForge global server SavedData with the identifier:

```text
wayfinder:civilization
```

Minecraft places it under the world's data storage and handles save timing.

## Runtime behavior

`/wayfinder analyze` now:

1. loads the current persisted CivilizationState
2. performs analysis and proposal validation
3. produces a transition
4. validates the transition
5. commits to a new immutable CivilizationState
6. writes that authoritative state back to SavedData
7. marks the data dirty only when the state actually changed

## Persistence test

After a qualifying node is committed:

1. note the `nodes N` value
2. save and quit the world
3. fully stop the dev client
4. run `.\gradlew.bat runClient` again
5. reopen the same world
6. run `/wayfinder analyze`

The previously committed node should already be present in network validation.
An equivalent proposal should not create duplicate history.

## Build/test

```powershell
.\gradlew.bat clean test
.\gradlew.bat build
.\gradlew.bat runClient
```

## Next

Once restart persistence is verified, the next logical milestone is the first
relationship model: an explicit directional relationship that can connect a
future DIRECTION Node to the persisted OBSERVATION Node.

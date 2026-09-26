# Milestone 53 — Persistent Regional Era State

Built against the uploaded current persistence package and exact
`HistoricalRegionKey` API.

The region key is the record:

```java
HistoricalRegionKey(int regionX, int regionZ)
```

and exposes `stableKey()` for its deterministic textual identity.

M53 adds:

- `WayfinderCivilizationEraSavedData`
- `MinecraftSavedDataCivilizationEraRepository`

Saved data ID:

```text
wayfinder:civilization_eras
```

Persistent schema:

```text
version
regions [
  region_x
  region_z
  era
]
```

The persisted list is sorted by region X/Z before storage so serialization is
stable even though the domain state deliberately does not require map ordering.

Unseen regions remain implicit Era 1 through `CivilizationEraState.eraFor`.
Only explicit regional era advancement needs to be persisted.

Duplicate persisted region entries fail loudly instead of silently choosing one.

M53 does not yet alter `/wayfinder generatehistory`. The live scope continues
using the current era source until M54 wires this repository into scope
derivation.

Apply after M52.1:

```powershell
.\gradlew.bat clean test
.\gradlew.bat build
```

No runClient test is required.

Next: M54 — derive live `HistoricalScope` from persisted regional era state and
add an explicit developer era-advance path.

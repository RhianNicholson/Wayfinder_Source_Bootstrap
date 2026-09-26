# Milestone 52 — Civilization Era Lifecycle

M51 validated the generalized historical engine as the live Minecraft path.

M52 begins the lifecycle phase by replacing the conceptual hardcoded `Era 1`
assumption with an explicit civilization-owned era model.

New domain types:

- `CivilizationEra`
- `CivilizationEraState`
- `CivilizationEraTransition`
- `CivilizationEraLifecycleService`

## Rules established

1. Era identity belongs to the Wayfinder civilization, not Minecraft time.
2. Each semantic historical region can be at its own current era.
3. An unseen region begins at Era 1.
4. Advancement is explicit and exactly one era at a time.
5. Advancement follows the existing mutation rule:
   `evaluate -> transition -> commit`.
6. A stale transition cannot overwrite a state that has already changed.

No Minecraft persistence or command wiring changes in M52. This isolates and
validates the lifecycle model before making era advancement persistent.

## Why region-owned era state?

Wayfinder regions represent separate geographic/historical contexts. Treating
the entire world as one global era would force unrelated civilizations to
advance merely because history changed elsewhere.

The model therefore stores:

```text
HistoricalRegionKey -> CivilizationEra
```

This also keeps era identity independent from chunks and world clock time.

## Validation

Apply after M51:

```powershell
.\gradlew.bat clean test
.\gradlew.bat build
```

No runClient test is required.

## Next

M53 will add persistent civilization-era state and derive live
`HistoricalScope` from the persisted regional era instead of the current
hardcoded era constant.

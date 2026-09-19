# Milestone 39 — Semantic Historical Scope

M38 validated live deterministic historical generation. Until now its RNG scope
used temporary literals:

- region: `global`
- era: `era:1`

M39 replaces that conceptually with explicit historical scope types:

- `HistoricalRegionKey`
- `HistoricalEraKey`
- `HistoricalScope`

## Region identity

The first implementation derives a stable 512 x 512 block historical region
from geography. It deliberately does **not** use Minecraft chunk identity.

This is a bootstrap identity, not the final semantic-region classifier. Later,
when the geography/civilization region lifecycle owns explicit region IDs,
`HistoricalRegionKey` can be constructed from that committed identity without
changing the downstream RNG contract.

Negative coordinates use floor division so region boundaries remain stable.

## Era identity

`HistoricalEraKey` is explicitly a civilization-history era. It is **not**
derived from Minecraft day count or current world time.

That prevents player waiting/sleeping from rewriting ancient Wayfinder history.

## Deterministic scope

The historical RNG can now be fed:

```text
world seed
+ HistoricalRegionKey
+ HistoricalEraKey
+ historical subsystem stage
+ generation version
```

Different regions and eras therefore receive independent deterministic streams.

## Test

Overlay after M38.1:

```powershell
.\gradlew.bat clean test
.\gradlew.bat build
```

No runClient test is required for this domain slice.

## Next

M40 will wire `HistoricalScope` into the live generation command/orchestrator,
using the invoking player's position to select the historical region while
keeping era identity explicit and civilization-owned.

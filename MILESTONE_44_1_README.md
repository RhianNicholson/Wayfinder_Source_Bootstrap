# Milestone 44.1 — DiscoverySequenceResult Restore

M44 replaced `DiscoverySequenceEvaluator` but referenced the result type without
including it in the patch. This restores the result contract used by the
evaluator:

```java
DiscoverySequenceResult(Set<DiscoveryBeat> beats, boolean coherent)
```

No M44 discovery semantics are changed.

Apply after M44:

```powershell
.\gradlew.bat clean test
.\gradlew.bat build
```

If compilation exposes a pre-existing result contract with different accessor
names, stop and report that compiler output before changing anything else.

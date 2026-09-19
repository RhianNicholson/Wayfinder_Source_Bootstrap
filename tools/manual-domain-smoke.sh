#!/usr/bin/env sh
set -eu
ROOT=$(CDPATH= cd -- "$(dirname -- "$0")/.." && pwd)
OUT="$ROOT/build/manual-smoke"
rm -rf "$OUT"
mkdir -p "$OUT"
find "$ROOT/src/main/java/com/wayfinder/core" \
     "$ROOT/src/main/java/com/wayfinder/geography" \
     "$ROOT/src/main/java/com/wayfinder/config" \
     "$ROOT/src/testSupport/java" \
     -name '*.java' -print > "$OUT/sources.txt"
javac -d "$OUT" @"$OUT/sources.txt" "$ROOT/tools/DomainSmokeTest.java"
java -cp "$OUT" DomainSmokeTest

#!/usr/bin/env sh
set -eu

BUILD_DIR="${TMPDIR:-/tmp}/game-digest-java-test"
rm -rf "$BUILD_DIR"
mkdir -p "$BUILD_DIR"
javac -d "$BUILD_DIR" $(find src/main/java src/test/java -name '*.java')
java -cp "$BUILD_DIR" learning.game.digest.DigestComposerTest

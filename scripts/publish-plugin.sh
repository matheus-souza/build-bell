#!/usr/bin/env bash
# Publishes the plugin to JetBrains Marketplace and exits 0 if the version
# is already present (idempotent upload).

VERSION="$1"
LOG_FILE="$(mktemp)"
trap 'rm -f "$LOG_FILE"' EXIT

./gradlew -PpluginVersion="$VERSION" buildPlugin signPlugin publishPlugin 2>&1 | tee "$LOG_FILE"
EXIT_CODE=${PIPESTATUS[0]}

if [ "$EXIT_CODE" -ne 0 ]; then
  if grep -qF 'already contains version' "$LOG_FILE"; then
    echo "Version $VERSION is already published — skipping duplicate upload."
    exit 0
  fi
  exit "$EXIT_CODE"
fi

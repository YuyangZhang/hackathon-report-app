#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "$0")/.." && pwd)"

for required in \
  "$ROOT_DIR/scripts/run-all-tests.sh" \
  "$ROOT_DIR/scripts/generate-test-report.sh" \
  "$ROOT_DIR/.github/workflows/test-matrix.yml" \
  "$ROOT_DIR/.github/workflows/test-smoke.yml"; do
  if [ ! -f "$required" ]; then
    echo "Missing required orchestration asset: $required" >&2
    exit 1
  fi
done

echo "Orchestration assets are present."

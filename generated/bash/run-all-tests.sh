#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "$0")/.." && pwd)"

run_backend() {
  echo "==> Backend tests"
  (cd "$ROOT_DIR/apps/gamerepo" && gradle test integrationTest apiTest jacocoAggregateReport)
}

run_frontend() {
  echo "==> Frontend tests"
  (cd "$ROOT_DIR/apps/gamerepo-ui" && npm test -- --runInBand)
}

run_contract() {
  echo "==> Contract tests"
  (cd "$ROOT_DIR/apps/gamerepo-ui" && npm run test:contract)
}

run_e2e() {
  echo "==> E2E tests"
  (cd "$ROOT_DIR/apps/gamerepo-ui" && npm run test:e2e)
}

run_backend
run_frontend
run_contract
run_e2e

echo "All test layers completed successfully."

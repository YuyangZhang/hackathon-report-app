#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "$0")/.." && pwd)"
REPORT_DIR="$ROOT_DIR/test-results"

mkdir -p "$REPORT_DIR/allure"
mkdir -p "$REPORT_DIR/coverage"

if [ -d "$ROOT_DIR/apps/gamerepo/build/reports/jacoco/jacocoAggregateReport" ]; then
  rm -rf "$REPORT_DIR/coverage/backend"
  mkdir -p "$REPORT_DIR/coverage/backend"
  cp -R "$ROOT_DIR/apps/gamerepo/build/reports/jacoco/jacocoAggregateReport/." "$REPORT_DIR/coverage/backend/"
fi

if [ -d "$ROOT_DIR/apps/gamerepo-ui/test-results/coverage" ]; then
  rm -rf "$REPORT_DIR/coverage/frontend"
  mkdir -p "$REPORT_DIR/coverage/frontend"
  cp -R "$ROOT_DIR/apps/gamerepo-ui/test-results/coverage/." "$REPORT_DIR/coverage/frontend/"
fi

find "$ROOT_DIR" -type d -path "*/allure-results*" -prune -print | while read -r source; do
  target="$REPORT_DIR/allure/$(basename "$(dirname "$source")")-$(basename "$source")"
  rm -rf "$target"
  mkdir -p "$target"
  cp -R "$source/." "$target/"
done

echo "Collected coverage and Allure-compatible artifacts in $REPORT_DIR"

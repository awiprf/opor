#!/usr/bin/env bash
# Long-running foreground launcher for the vec Verification Email Checker.
# Runs as a Cloud Agent terminal so its logs stay visible and it can be
# restarted independently. Uses application.yml defaults, which already point
# at the local PostgreSQL/Redis brought up by start.sh.
set -euo pipefail

REPO_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
GRD_DIR="$REPO_ROOT/src/svr/src/jsb/grd"

cd "$GRD_DIR"
exec ./gradlew :svc:aut:sgu:usn:vec:bootRun \
  --args='--spring.profiles.active=dev' --no-daemon

#!/usr/bin/env bash
# Idempotent dependency refresh + compile for the opor server.
# Runs after the repo is checked out. Must not depend on running services
# (PostgreSQL/Redis are brought up later by start.sh), so tests are skipped
# here and executed during validation once services are live.
set -euo pipefail

REPO_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
GRD_DIR="$REPO_ROOT/src/svr/src/jsb/grd"

cd "$GRD_DIR"
chmod +x ./gradlew
./gradlew :svc:aut:sgu:usn:vec:build -x test --no-daemon

echo "[install] vec build complete."

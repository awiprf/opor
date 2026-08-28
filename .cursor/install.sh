#!/usr/bin/env bash
# Idempotent environment bootstrap for the opor server.
#
# Runs from /workspace after checkout. Installs the system services the vec
# microservice needs (PostgreSQL + Redis) and compiles the Gradle project so
# dependencies are cached. It must NOT depend on running services: PostgreSQL
# and Redis are started later by start.sh, so tests (which need live services)
# are skipped here and executed during validation once services are up.
#
# Java 21 is already present on the base image; this script only adds what the
# base image lacks. Safe to re-run.
set -euo pipefail

REPO_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
GRD_DIR="$REPO_ROOT/src/svr/src/jsb/grd"

# --- System dependencies (idempotent) -------------------------------------
# Install PostgreSQL + Redis only when missing so re-runs are fast.
if ! command -v psql >/dev/null 2>&1 || ! command -v redis-server >/dev/null 2>&1; then
  echo "[install] Installing PostgreSQL and Redis..."
  sudo apt-get update -y
  sudo DEBIAN_FRONTEND=noninteractive apt-get install -y \
    postgresql postgresql-contrib redis-server
else
  echo "[install] PostgreSQL and Redis already present."
fi

# --- Application build ------------------------------------------------------
cd "$GRD_DIR"
chmod +x ./gradlew
./gradlew :svc:aut:sgu:usn:vec:build -x test --no-daemon

echo "[install] vec build complete."

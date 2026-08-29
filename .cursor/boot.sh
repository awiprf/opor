#!/usr/bin/env bash
# Per-boot entrypoint used as the environment `start` command.
# Reconciles dependencies (PostgreSQL, Redis, seed data) and then runs the
# vec microservice attached in the foreground so the platform keeps it alive
# and its logs stay visible.
set -euo pipefail

DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

bash "$DIR/start.sh"
exec bash "$DIR/run-vec.sh"

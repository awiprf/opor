#!/usr/bin/env bash
# Per-boot reconciliation for the opor vec microservice dependencies.
# Brings up PostgreSQL and Redis, ensures the aut database/role/schema exist,
# and seeds a known-registered email (both in PostgreSQL and the Redis Bloom
# filter) so the service can demonstrate both available:true and
# available:false locally. Idempotent and safe to re-run on every boot.
set -euo pipefail

REPO_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"

DB_NAME="aut_db"
DB_USER="local_user"
DB_PASS="local_pass"
SEED_EMAIL="existing@example.com"
BLOOM_KEY="aut:email_bloom"

echo "[start] Starting PostgreSQL..."
sudo service postgresql start

echo "[start] Starting Redis..."
sudo service redis-server start

# Wait until PostgreSQL accepts connections.
for _ in $(seq 1 30); do
  if sudo -u postgres pg_isready -q; then break; fi
  sleep 1
done

echo "[start] Ensuring role and database exist..."
sudo -u postgres psql -tAc "SELECT 1 FROM pg_roles WHERE rolname='${DB_USER}'" | grep -q 1 \
  || sudo -u postgres psql -c "CREATE ROLE ${DB_USER} LOGIN PASSWORD '${DB_PASS}';"
sudo -u postgres psql -tAc "SELECT 1 FROM pg_database WHERE datname='${DB_NAME}'" | grep -q 1 \
  || sudo -u postgres createdb -O "${DB_USER}" "${DB_NAME}"

echo "[start] Ensuring schema and seed data..."
sudo -u postgres psql -d "${DB_NAME}" <<SQL
CREATE EXTENSION IF NOT EXISTS pgcrypto;
CREATE TABLE IF NOT EXISTS users (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    email VARCHAR(255) UNIQUE NOT NULL
);
ALTER TABLE users OWNER TO ${DB_USER};
INSERT INTO users (email) VALUES ('${SEED_EMAIL}') ON CONFLICT (email) DO NOTHING;
SQL

echo "[start] Seeding Redis Bloom filter bits for ${SEED_EMAIL}..."
while read -r offset; do
  [ -n "$offset" ] && redis-cli SETBIT "${BLOOM_KEY}" "$offset" 1 >/dev/null
done < <(python3 "${REPO_ROOT}/.cursor/seed_bloom.py" "${SEED_EMAIL}")

echo "[start] Dependencies ready (PostgreSQL:5432, Redis:6379, ${DB_NAME} seeded)."

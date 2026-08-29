#!/usr/bin/env python3
"""Compute vec Bloom-filter bit offsets for an email and print SETBIT args.

This mirrors `BloomFilterUtil.getOffsets` in the vec microservice
(Kirsch-Mitzenmacher double hashing over a custom MurmurHash3 x86_32).
It exists only to seed local/dev Redis so that a known-registered email
resolves through the Bloom-filter "hit" slow path to the PostgreSQL check,
letting developers exercise the `available:false` response locally.

Usage:
    python3 seed_bloom.py <email> [bit_array_size] [k]
Prints one offset per line.
"""
import sys

BIT_ARRAY_SIZE = 100_000_000
HASH_FUNCTIONS_COUNT = 5
MASK32 = 0xFFFFFFFF

C1 = 0xCC9E2D51
C2 = 0x1B873593


def _rotl32(x: int, r: int) -> int:
    x &= MASK32
    return ((x << r) | (x >> (32 - r))) & MASK32


def _murmur332(data: bytes, seed: int) -> int:
    h = seed & MASK32
    length = len(data)
    i = 0
    while i <= length - 4:
        k = (data[i]) | (data[i + 1] << 8) | (data[i + 2] << 16) | (data[i + 3] << 24)
        k = (k * C1) & MASK32
        k = _rotl32(k, 15)
        k = (k * C2) & MASK32
        h ^= k
        h = _rotl32(h, 13)
        h = (h * 5 + 0xE6546B64) & MASK32
        i += 4
    k1 = 0
    tail = length & 3
    if tail == 3:
        k1 ^= data[i + 2] << 16
    if tail >= 2:
        k1 ^= data[i + 1] << 8
    if tail >= 1:
        k1 ^= data[i]
        k1 = (k1 * C1) & MASK32
        k1 = _rotl32(k1, 15)
        k1 = (k1 * C2) & MASK32
        h ^= k1
    h ^= length
    h ^= h >> 16
    h = (h * 0x85EBCA6B) & MASK32
    h ^= h >> 13
    h = (h * 0xC2B2AE35) & MASK32
    h ^= h >> 16
    return h & MASK32


def get_offsets(email: str, array_size: int = BIT_ARRAY_SIZE, k: int = HASH_FUNCTIONS_COUNT):
    data = email.strip().lower().encode("utf-8")
    hash1 = _murmur332(data, 0)
    hash2 = _murmur332(data, hash1)
    return [((hash1 + i * hash2) & 0x7FFFFFFFFFFFFFFF) % array_size for i in range(k)]


if __name__ == "__main__":
    if len(sys.argv) < 2:
        print("usage: seed_bloom.py <email> [bit_array_size] [k]", file=sys.stderr)
        sys.exit(1)
    email = sys.argv[1]
    size = int(sys.argv[2]) if len(sys.argv) > 2 else BIT_ARRAY_SIZE
    kk = int(sys.argv[3]) if len(sys.argv) > 3 else HASH_FUNCTIONS_COUNT
    for off in get_offsets(email, size, kk):
        print(off)

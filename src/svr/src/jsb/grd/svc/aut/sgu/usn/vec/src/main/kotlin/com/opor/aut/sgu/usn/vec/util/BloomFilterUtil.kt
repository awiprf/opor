package com.opor.aut.sgu.usn.vec.util

import java.nio.charset.StandardCharsets

object BloomFilterUtil {

    const val BIT_ARRAY_SIZE: Long = 100_000_000L
    private const val HASH_FUNCTIONS_COUNT: Int = 5

    /**
     * Calculates K bit positions (offsets) using Kirsch-Mitzenmacher double hashing:
     * Hash_i(x) = (hash1 + i * hash2) % M
     */
    fun getOffsets(
        element: String,
        arraySize: Long = BIT_ARRAY_SIZE,
        k: Int = HASH_FUNCTIONS_COUNT
    ): LongArray {
        val bytes = element.trim().lowercase().toByteArray(StandardCharsets.UTF_8)

        val hash1 = murmur332(bytes, 0)
        val hash2 = murmur332(bytes, hash1.toInt())

        val offsets = LongArray(k)
        for (i in 0 until k) {
            // Compute combined hash as Long to avoid overflow and type mismatch
            val combinedHash = hash1 + (i.toLong() * hash2)
            // Mask out sign bit and perform modulo against arraySize
            offsets[i] = (combinedHash and Long.MAX_VALUE) % arraySize
        }
        return offsets
    }

    private fun murmur332(data: ByteArray, seed: Int): Long {
        var h = seed
        val length = data.size
        var i = 0
        while (i <= length - 4) {
            var k = (data[i].toInt() and 0xFF) or
                    ((data[i + 1].toInt() and 0xFF) shl 8) or
                    ((data[i + 2].toInt() and 0xFF) shl 16) or
                    ((data[i + 3].toInt() and 0xFF) shl 24)
            k *= 0xcc9e2d51.toInt()
            k = Integer.rotateLeft(k, 15)
            k *= 0x1b873593.toInt()

            h = h xor k
            h = Integer.rotateLeft(h, 13)
            h = h * 5 + 0xe6546b64.toInt()
            i += 4
        }
        var k1 = 0
        val tail = length and 3
        if (tail == 3) k1 = k1 xor ((data[i + 2].toInt() and 0xFF) shl 16)
        if (tail >= 2) k1 = k1 xor ((data[i + 1].toInt() and 0xFF) shl 8)
        if (tail >= 1) {
            k1 = k1 xor (data[i].toInt() and 0xFF)
            k1 *= 0xcc9e2d51.toInt()
            k1 = Integer.rotateLeft(k1, 15)
            k1 *= 0x1b873593.toInt()
            h = h xor k1
        }
        h = h xor length
        h = h xor (h ushr 16)
        h *= 0x85ebca6b.toInt()
        h = h xor (h ushr 13)
        h *= 0xc2b2ae35.toInt()
        h = h xor (h ushr 16)
        return h.toLong() and 0xFFFFFFFFL
    }
}
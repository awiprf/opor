package com.opor.aut.sgu.usn.vec.service

import com.opor.aut.sgu.usn.vec.repository.EmailRecordRepository
import com.opor.aut.sgu.usn.vec.util.BloomFilterUtil
import org.slf4j.LoggerFactory
import org.springframework.data.redis.core.RedisCallback
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service
import java.util.concurrent.TimeUnit

@Service
class EmailCheckService(
    private val emailRecordRepository: EmailRecordRepository,
    private val redisTemplate: RedisTemplate<String, String>
) {

    private val logger = LoggerFactory.getLogger(EmailCheckService::class.java)

    companion object {
        private const val BLOOM_FILTER_KEY = "aut:email_bloom"
        private const val CACHE_PREFIX = "vec:email:"
        private const val CACHE_TTL_SECONDS = 60L
        private const val TAKEN = "1"
        private const val NOT_TAKEN = "0"
    }

    fun isEmailTaken(email: String): Boolean {
        val cacheKey = "$CACHE_PREFIX$email"

        // 1. Tier 1 Fast Path: Check 60-second KV cache
        val cached = redisTemplate.opsForValue().get(cacheKey)
        if (cached != null) {
            logger.debug("KV Cache hit for email check: {}", email)
            return cached == TAKEN
        }

        // 2. Tier 2 Fast Path: Check Bloom Filter offsets in Redis
        val offsets = BloomFilterUtil.getOffsets(email)
        logger.info("OFFSETS FOR {}: {}", email, offsets.joinToString(","))
        val bitResults = redisTemplate.executePipelined(RedisCallback<Any> { connection ->
            val keyBytes = BLOOM_FILTER_KEY.toByteArray(Charsets.UTF_8)
            for (offset in offsets) {
                connection.stringCommands().getBit(keyBytes, offset)
            }
            null
        })

        val allBitsSet = bitResults.all { it == true || it == 1L }
        if (!allBitsSet) {
            logger.debug("Bloom Filter Fast Path: Email {} is AVAILABLE", email)
            redisTemplate.opsForValue().set(cacheKey, NOT_TAKEN, CACHE_TTL_SECONDS, TimeUnit.SECONDS)
            return false
        }

        // 3. Slow Path: Bloom Filter hit -> Check PostgreSQL
        logger.debug("Bloom Filter Hit for {}: verifying with database", email)
        val exists = emailRecordRepository.existsByEmail(email)

        // 4. Cache DB result for 60 seconds
        val value = if (exists) TAKEN else NOT_TAKEN
        redisTemplate.opsForValue().set(cacheKey, value, CACHE_TTL_SECONDS, TimeUnit.SECONDS)

        return exists
    }
}
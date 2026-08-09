// package com.opor.aut.sgu.usn.vec.service

// import com.opor.aut.sgu.usn.vec.repository.EmailRecordRepository
// import org.slf4j.LoggerFactory
// import org.springframework.data.redis.core.RedisTemplate
// import org.springframework.stereotype.Service
// import java.util.concurrent.TimeUnit

// /**
//  * Service layer for email availability checks.
//  *
//  * Implements a cache-aside pattern:
//  * 1. Check Redis cache first (fast path)
//  * 2. On cache miss, query PostgreSQL via [EmailRecordRepository]
//  * 3. Cache the result in Redis with a 60-second TTL
//  *
//  * This ensures the database is protected from repetitive
//  * read-heavy traffic during signup flows.
//  */
// @Service
// class EmailCheckService(
//     private val emailRecordRepository: EmailRecordRepository,
//     private val redisTemplate: RedisTemplate<String, String>
// ) {

//     private val logger = LoggerFactory.getLogger(EmailCheckService::class.java)

//     companion object {
//         private const val CACHE_PREFIX = "vec:email:"
//         private const val CACHE_TTL_SECONDS = 60L
//         private const val TAKEN = "1"
//         private const val NOT_TAKEN = "0"
//     }

//     /**
//      * Checks whether the given email address is already registered.
//      *
//      * @param email normalised (lowercase, trimmed) email address
//      * @return true if the email is already taken, false if available
//      */
//     fun isEmailTaken(email: String): Boolean {
//         val cacheKey = "$CACHE_PREFIX$email"

//         // 1. Check Redis cache
//         val cached = redisTemplate.opsForValue().get(cacheKey)
//         if (cached != null) {
//             logger.debug("Cache hit for email check: {}", email)
//             return cached == TAKEN
//         }

//         // 2. Cache miss — query database
//         logger.debug("Cache miss for email check: {}, querying database", email)
//         val exists = emailRecordRepository.existsByEmail(email)

//         // 3. Cache the result
//         val value = if (exists) TAKEN else NOT_TAKEN
//         redisTemplate.opsForValue().set(cacheKey, value, CACHE_TTL_SECONDS, TimeUnit.SECONDS)

//         return exists
//     }
// }

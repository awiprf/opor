package com.opor.aut.sgu.usn.vec.config

import com.opor.aut.sgu.usn.vec.exception.RateLimitExceededException
import io.github.bucket4j.Bandwidth
import io.github.bucket4j.Bucket
import io.github.bucket4j.Refill
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.HandlerInterceptor
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import java.time.Duration
import java.util.concurrent.ConcurrentHashMap

/**
 * Rate limiting configuration using Bucket4j.
 *
 * Limits each client IP to a maximum of 10 email check
 * requests per minute. This prevents enumeration attacks
 * where an attacker probes the system to discover which
 * email addresses are registered.
 */
@Configuration
class RateLimitConfig : WebMvcConfigurer {

    override fun addInterceptors(registry: InterceptorRegistry) {
        registry.addInterceptor(RateLimitInterceptor())
            .addPathPatterns("/api/v1/aut/check-email")
    }
}

/**
 * HTTP interceptor that enforces per-IP rate limiting
 * on the email check endpoint.
 */
class RateLimitInterceptor : HandlerInterceptor {

    private val buckets = ConcurrentHashMap<String, Bucket>()

    companion object {
        private const val MAX_REQUESTS = 10L
        private val REFILL_PERIOD: Duration = Duration.ofMinutes(1)
    }

    override fun preHandle(
        request: HttpServletRequest,
        response: HttpServletResponse,
        handler: Any
    ): Boolean {
        val clientIp = resolveClientIp(request)
        val bucket = buckets.computeIfAbsent(clientIp) { createBucket() }

        if (!bucket.tryConsume(1)) {
            throw RateLimitExceededException()
        }

        return true
    }

    private fun createBucket(): Bucket {
        val bandwidth = Bandwidth.classic(
            MAX_REQUESTS,
            Refill.greedy(MAX_REQUESTS, REFILL_PERIOD)
        )
        return Bucket.builder().addLimit(bandwidth).build()
    }

    private fun resolveClientIp(request: HttpServletRequest): String {
        return request.getHeader("X-Forwarded-For")?.split(",")?.firstOrNull()?.trim()
            ?: request.remoteAddr
    }
}

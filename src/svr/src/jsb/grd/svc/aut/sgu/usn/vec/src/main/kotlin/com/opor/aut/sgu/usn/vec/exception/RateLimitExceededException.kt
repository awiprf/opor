package com.opor.aut.sgu.usn.vec.exception

/**
 * Thrown when a client exceeds the allowed rate limit
 * for the email check endpoint.
 */
class RateLimitExceededException(
    message: String = "Rate limit exceeded. Please try again later."
) : RuntimeException(message)

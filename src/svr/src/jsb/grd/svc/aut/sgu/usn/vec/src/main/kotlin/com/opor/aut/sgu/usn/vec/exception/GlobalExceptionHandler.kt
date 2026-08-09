package com.opor.aut.sgu.usn.vec.exception

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MissingServletRequestParameterException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

/**
 * Centralised exception handler for the VEC microservice.
 *
 * Maps known exceptions to appropriate HTTP status codes
 * and structured error responses.
 */
@ControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(RateLimitExceededException::class)
    fun handleRateLimitExceeded(ex: RateLimitExceededException): ResponseEntity<Map<String, String>> {
        return ResponseEntity
            .status(HttpStatus.TOO_MANY_REQUESTS)
            .body(mapOf("error" to (ex.message ?: "Rate limit exceeded.")))
    }

    @ExceptionHandler(MissingServletRequestParameterException::class)
    fun handleMissingParam(ex: MissingServletRequestParameterException): ResponseEntity<Map<String, String>> {
        return ResponseEntity
            .badRequest()
            .body(mapOf("error" to "Missing required parameter: ${ex.parameterName}"))
    }

    @ExceptionHandler(Exception::class)
    fun handleGenericException(ex: Exception): ResponseEntity<Map<String, String>> {
        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(mapOf("error" to "An unexpected error occurred."))
    }
}

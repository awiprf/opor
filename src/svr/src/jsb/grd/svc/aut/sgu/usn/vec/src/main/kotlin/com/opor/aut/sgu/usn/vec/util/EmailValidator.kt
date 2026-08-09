package com.opor.aut.sgu.usn.vec.util

/**
 * Backend email format validation utility.
 *
 * This provides a secondary validation layer behind the frontend's
 * regex check. It guards against malformed inputs that bypass
 * client-side validation (e.g., direct API calls, bots).
 */
object EmailValidator {

    // RFC 5322 simplified — intentionally permissive to avoid
    // rejecting valid edge-case addresses. Strict validation
    // happens when we actually send the verification email.
    private val EMAIL_REGEX = Regex(
        "^[A-Za-z0-9._%+\\-]+@[A-Za-z0-9.\\-]+\\.[A-Za-z]{2,}$"
    )

    /**
     * Validates that the given string looks like a plausible email address.
     *
     * @param email the raw email input
     * @return true if the format is valid
     */
    fun isValid(email: String): Boolean {
        if (email.isBlank()) return false
        if (email.length > 254) return false
        return EMAIL_REGEX.matches(email.trim())
    }
}

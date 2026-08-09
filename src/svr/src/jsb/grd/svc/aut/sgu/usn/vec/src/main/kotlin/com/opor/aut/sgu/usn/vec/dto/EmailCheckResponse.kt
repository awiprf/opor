package com.opor.aut.sgu.usn.vec.dto

/**
 * Response DTO for the email availability check endpoint.
 *
 * @property email the normalised email address that was checked
 * @property available true if the email is not yet registered
 */
data class EmailCheckResponse(
    val email: String,
    val available: Boolean
)

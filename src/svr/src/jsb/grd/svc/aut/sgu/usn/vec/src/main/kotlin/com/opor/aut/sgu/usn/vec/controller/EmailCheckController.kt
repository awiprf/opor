package com.opor.aut.sgu.usn.vec.controller

import com.opor.aut.sgu.usn.vec.dto.EmailCheckResponse
import com.opor.aut.sgu.usn.vec.service.EmailCheckService
import com.opor.aut.sgu.usn.vec.util.EmailValidator
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

/**
 * REST controller for checking email availability.
 *
 * Endpoint: GET /api/v1/aut/check-email?email=user@example.com
 *
 * This is the first step in the signup flow (service #1 in the
 * usn provider sequence). It validates the email format and
 * checks whether the address is already registered.
 */
@RestController
@RequestMapping("/api/v1/aut")
class EmailCheckController(
    private val emailCheckService: EmailCheckService
) {

    @GetMapping("/check-email")
    fun checkEmail(@RequestParam email: String): ResponseEntity<EmailCheckResponse> {
        if (!EmailValidator.isValid(email)) {
            return ResponseEntity.badRequest().build()
        }

        val normalizedEmail = email.trim().lowercase()
        val isTaken = emailCheckService.isEmailTaken(normalizedEmail)

        return ResponseEntity.ok(
            EmailCheckResponse(
                email = normalizedEmail,
                available = !isTaken
            )
        )
    }
}

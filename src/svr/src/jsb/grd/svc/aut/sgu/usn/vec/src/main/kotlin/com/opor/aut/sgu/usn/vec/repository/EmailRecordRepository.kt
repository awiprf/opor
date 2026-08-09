package com.opor.aut.sgu.usn.vec.repository

import com.opor.aut.sgu.usn.vec.model.EmailRecord
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

/**
 * Repository for email existence checks against the `users` table.
 *
 * Uses Spring Data JPA's derived query method to generate an
 * efficient `SELECT EXISTS(...)` query with the unique index
 * on the email column.
 */
@Repository
//interface EmailRecordRepository : JpaRepository<EmailRecord, Long> {
interface EmailRecordRepository : JpaRepository<EmailRecord, UUID> {

    /**
     * Checks whether a user record with the given email exists.
     * Translates to: SELECT CASE WHEN COUNT(*) > 0 THEN true ELSE false END
     *                FROM users WHERE email = :email
     */
    fun existsByEmail(email: String): Boolean
}

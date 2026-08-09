package com.opor.aut.sgu.usn.vec.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.util.UUID

/**
 * Slim entity mapping to the `users` table.
 *
 * This entity intentionally only maps the columns that the
 * Verification Email Checker (vec) microservice needs:
 * the primary key and the email address.
 *
 * Other microservices that interact with the same table
 * define their own entity with only the columns they require.
 */
@Entity
@Table(name = "users")
class EmailRecord(

    @Id
    //@GeneratedValue(strategy = GenerationType.IDENTITY)
    //val id: Long = 0,
    val id: UUID = UUID.randomUUID(),

    @Column(unique = true, nullable = false)
    val email: String = ""
)

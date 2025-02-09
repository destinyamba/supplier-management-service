package com.supplier_management_service.supplier_management_service.dtos.request

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.Instant
import java.util.*

@Document
data class PasswordResetToken(
    @Id
    val id: UUID = UUID.randomUUID(),
    val token: String,
    val userId: String,
    val expiryDate: Instant
)


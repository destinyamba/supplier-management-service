package com.supplier_management_service.supplier_management_service.models

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime

@Document("pendingUsers")
data class PendingUser(
    @Id
    val id: String? = null,
    val email: String,
    val name: String,
    val role: Role,
    var orgId: String,
    val orgName: String? = null,
    val businessType: BusinessType? = null,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val lastSignIn: LocalDateTime = LocalDateTime.now(),
)


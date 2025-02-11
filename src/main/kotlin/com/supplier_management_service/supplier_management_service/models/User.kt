package com.supplier_management_service.supplier_management_service.models

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime

@Document(collection = "users")
data class User(
    @Id
    val id: String? = null,
    val email: String,
    var password: String,
    val name: String,
    val role: Role,
    val businessType: BusinessType,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val lastSignIn: LocalDateTime = LocalDateTime.now(),
    var resetToken: String? = null
)

enum class BusinessType(val value: String) {
    CLIENT("CLIENT"), SUPPLIER("SUPPLIER")
}

enum class Role {
    ADMIN, EDITOR, VIEWER
}

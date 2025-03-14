package com.supplier_management_service.supplier_management_service.models

import com.nimbusds.openid.connect.sdk.assurance.evidences.Organization
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime

@Document(collection = "users")
data class User(
    @Id
    val id: String? = null,
    var email: String,
    var password: String,
    var name: String,
    var role: Role,
    val businessType: BusinessType,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val lastSignIn: LocalDateTime = LocalDateTime.now(),
    var resetToken: String? = null,
    var organizationName: String? = null,
    var orgId: String? = null
)

data class UserDetails(
    val userId: String,
    val email: String,
    val name: String,
    val role: Role,
    val userType: String,
    var organizationName: String? = null,
    var lastSignIn: LocalDateTime,
    var createdAt: LocalDateTime,
    var orgId: String? = null
)

data class UserRequest(val userEmail: String)

enum class BusinessType(val value: String) {
    CLIENT("CLIENT"), SUPPLIER("SUPPLIER")
}

enum class Role {
    ADMIN, EDITOR, VIEWER
}

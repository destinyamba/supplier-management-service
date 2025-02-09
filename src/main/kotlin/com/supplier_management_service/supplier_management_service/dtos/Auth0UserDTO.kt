package com.supplier_management_service.supplier_management_service.dtos

import org.springframework.data.annotation.Id

data class Auth0UserRequest(
    val email: String,
    val phoneNumber: String? = null,
    val userMetadata: Map<String, Any>? = null,
    val appMetadata: Map<String, Any>? = null,
    val givenName: String,
    val familyName: String,
    val name: String,
    val nickname: String,
    val picture: String? = null,
    val userId: String? = null,
    val connection: String,
    val password: String,
    val verifyEmail: Boolean = false,
    val username: String? = null
)

data class MongoUser(
    @Id val id: String? = null,
    val email: String,
    val userName: String,
    val name: String,
    val nickname: String,
    val givenName: String,
    val familyName: String,
    val connection: String,
    val phoneNumber: String?
)

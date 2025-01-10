package com.supplier_management_service.supplier_management_service.models

import com.fasterxml.jackson.annotation.JsonProperty

data class Auth0User(
    val email: String,
    @JsonProperty("user_id")
    val userId: String,
    val name: String?,
    val nickname: String?,
    @JsonProperty("created_at")
    val createdAt: String,
    @JsonProperty("updated_at")
    val updatedAt: String,
    @JsonProperty("email_verified")
    val emailVerified: Boolean,
    val picture: String?,
    @JsonProperty("user_metadata")
    val userMetadata: Map<String, Any>? = null,
    @JsonProperty("app_metadata")
    val appMetadata: Map<String, Any>? = null
)

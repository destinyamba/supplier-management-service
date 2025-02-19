package com.supplier_management_service.supplier_management_service.dtos.request

import jakarta.validation.constraints.NotBlank


data class PasswordResetRequest(
    @field:NotBlank(message = "Email is required when token is not provided")
    val email: String?,
    val token: String?,
    @field:NotBlank(message = "New password is required when token is provided")
    val newPassword: String?
)

data class MessageResponse(val message: String)

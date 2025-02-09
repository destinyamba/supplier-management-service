package com.supplier_management_service.supplier_management_service.dtos.request


data class ForgotPasswordRequest(
    val email: String
)

data class PasswordResetRequest(
    val email: String,
    val token: String? = null,
    val newPassword: String? = null
)

data class MessageResponse(val message: String)

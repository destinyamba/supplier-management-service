package com.supplier_management_service.supplier_management_service.dtos.response

import com.supplier_management_service.supplier_management_service.models.BusinessType
import com.supplier_management_service.supplier_management_service.models.Role
import java.time.LocalDateTime

data class SignupResponse(
    val id: String,
    val email: String,
    val name: String,
    val role: Role,
    val businessType: BusinessType,
    val createdAt: LocalDateTime,
    val lastSignIn: LocalDateTime
)
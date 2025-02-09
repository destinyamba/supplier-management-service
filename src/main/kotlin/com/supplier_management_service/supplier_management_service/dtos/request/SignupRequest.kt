package com.supplier_management_service.supplier_management_service.dtos.request

import com.supplier_management_service.supplier_management_service.models.BusinessType
import com.supplier_management_service.supplier_management_service.models.Role
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import org.jetbrains.annotations.NotNull

data class SignupRequest(
    @field:NotBlank
    @field:Email
    val email: String,

    @field:NotBlank
    @field:Size(min = 6)
    val password: String,

    @field:NotBlank
    val name: String,

    @field:NotNull
    val role: Role? = Role.ADMIN,

    @field:NotNull
    val businessType: BusinessType
)
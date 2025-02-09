package com.supplier_management_service.supplier_management_service.dtos.response

import com.supplier_management_service.supplier_management_service.models.BusinessType
import com.supplier_management_service.supplier_management_service.models.Role

data class SigninResponse(
    val id: String,
    val email: String,
    val name: String,
    val role: Role,
    val businessType: BusinessType,
    val token: String,
)

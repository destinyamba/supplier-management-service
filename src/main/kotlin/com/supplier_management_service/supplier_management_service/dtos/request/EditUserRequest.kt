package com.supplier_management_service.supplier_management_service.dtos.request

import com.supplier_management_service.supplier_management_service.models.Role

data class EditUserRequest(
    val userId: String,
    val newName: String?,
    val newEmail: String?,
    val newRole: Role?
)

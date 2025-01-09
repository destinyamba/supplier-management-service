package com.supplier_management_service.supplier_management_service.services

import org.springframework.stereotype.Service

@Service
class UserManagementService {
    fun getUser(): String {
        val text = "This is a public message."

        return text
    }

    fun getProtectedUser(): String {
        val text = "This is a privates message."

        return text
    }

    fun getAdminUser(): String {
        val text = "This is an admin message."

        return text
    }
}

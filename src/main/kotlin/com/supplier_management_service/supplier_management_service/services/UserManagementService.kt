package com.supplier_management_service.supplier_management_service.services

import com.supplier_management_service.supplier_management_service.models.UserDetails
import com.supplier_management_service.supplier_management_service.repositories.SupplierRepository
import com.supplier_management_service.supplier_management_service.repositories.UserRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class UserManagementService(private val userRepository: UserRepository, private val supplierRepository: SupplierRepository) {
    private val logger = LoggerFactory.getLogger(UserManagementService::class.java)

    fun getUserDetails(userEmail: String): UserDetails? {
        val getUser = userRepository.findByEmail(userEmail)

        val userDetails = getUser?.let {
            UserDetails(
                name = it.name,
                email = it.email,
                role = it.role,
                organizationName = it.organizationName!!
            )
        }

        return userDetails
    }

    // show list of users in an organization
    // invite user into an organization
}

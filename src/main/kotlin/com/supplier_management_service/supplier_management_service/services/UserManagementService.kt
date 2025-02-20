package com.supplier_management_service.supplier_management_service.services

import com.supplier_management_service.supplier_management_service.models.BusinessType
import com.supplier_management_service.supplier_management_service.models.UserDetails
import com.supplier_management_service.supplier_management_service.repositories.ClientRepository
import com.supplier_management_service.supplier_management_service.repositories.SupplierRepository
import com.supplier_management_service.supplier_management_service.repositories.UserRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class UserManagementService(
    private val userRepository: UserRepository,
    private val supplierRepository: SupplierRepository,
    private val clientRepository: ClientRepository
) {
    private val logger = LoggerFactory.getLogger(UserManagementService::class.java)

    fun getUserDetails(userEmail: String): UserDetails? {
        if (userRepository.existsByEmail(userEmail).not()) {
            throw IllegalArgumentException("User not found")
        }

        val user = userRepository.findByEmail(userEmail)

        // Create UserDetails with potentially null organizationName
        val userDetails = user?.let {
            UserDetails(
                name = it.name,
                email = it.email,
                role = it.role,
                userType = it.businessType.value,
                organizationName = it.organizationName
            )
        }

        updateUserDetailsOrganizationName(userDetails!!, userEmail)

        return userDetails
    }

    // If organizationName is null, try to find and update from client or supplier
    private fun updateUserDetailsOrganizationName(userDetails: UserDetails, userEmail: String) {
        val user = userRepository.findByEmail(userEmail)
        if (userDetails.organizationName == null) {
            when (user?.businessType) {
                BusinessType.CLIENT -> {
                    val client = clientRepository.findByContactInfo_PrimaryContact_PrimaryContactEmail(userEmail)
                    client?.let {
                        userDetails.organizationName = it.clientName
                        // Update user entity with organization name
                        user.organizationName = it.clientName
                        userRepository.save(user)
                    }
                }

                BusinessType.SUPPLIER -> {
                    val supplier = supplierRepository.findByContactInfo_PrimaryContact_PrimaryContactEmail(userEmail)
                    supplier?.let {
                        userDetails.organizationName = it.supplierName
                        // Update user entity with organization name
                        user.organizationName = it.supplierName
                        userRepository.save(user)
                    }
                }

                else -> {
                    logger.warn("Unable to determine organization name for user: $userEmail")
                }
            }
        }
    }

    // show list of users in an organization
    // invite user into an organization
}

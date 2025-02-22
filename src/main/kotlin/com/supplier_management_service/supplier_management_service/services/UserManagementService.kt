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
                userId = it.id!!,
                name = it.name,
                email = it.email,
                role = it.role,
                userType = it.businessType.value,
                organizationName = it.organizationName,
                lastSignIn = it.lastSignIn,
                createdAt = it.createdAt,
                orgId = it.orgId
            )
        }

        updateUserDetailsOrganizationName(userDetails!!, userEmail)

        return userDetails
    }

    // If organizationName is null, try to find and update from client or supplier
    private fun updateUserDetailsOrganizationName(userDetails: UserDetails, userEmail: String) {
        val user = userRepository.findByEmail(userEmail)
        if (userDetails.organizationName == null || userDetails.orgId == null) {
            when (user?.businessType) {
                BusinessType.CLIENT -> {
                    val client = clientRepository.findByContactInfo_PrimaryContact_PrimaryContactEmail(userEmail)
                    client?.let {
                        userDetails.organizationName = it.clientName
                        userDetails.orgId = it.id
                        // Update user entity with organization name
                        user.organizationName = it.clientName
                        user.orgId = it.id
                        userRepository.save(user)
                    }
                }

                BusinessType.SUPPLIER -> {
                    val supplier = supplierRepository.findByContactInfo_PrimaryContact_PrimaryContactEmail(userEmail)
                    supplier?.let {
                        userDetails.organizationName = it.supplierName
                        userDetails.orgId = it.id
                        // Update user entity with organization name
                        user.organizationName = it.supplierName
                        user.orgId = it.id
                        userRepository.save(user)
                    }
                }

                else -> {
                    logger.warn("Unable to determine organization name for user: $userEmail")
                }
            }
        }
    }

    fun usersUnderOrg(orgId: String): List<UserDetails> {
        val findSupplier = supplierRepository.findSupplierById(orgId)
        val findClient = clientRepository.findClientById(orgId)
        val findUser = userRepository.findByOrgId(orgId)

        val userDetailsList = mutableListOf<UserDetails>()

        findUser.forEach { user ->
            if (user.orgId == findClient?.id || user.orgId == findSupplier?.id) {
                val userDetails =
                    UserDetails(
                        userId = user.id!!,
                        name = user.name,
                        email = user.email,
                        role = user.role,
                        userType = user.businessType.value,
                        organizationName = user.organizationName,
                        lastSignIn = user.lastSignIn,
                        createdAt = user.createdAt,
                        orgId = user.orgId
                    )
                userDetailsList.add(userDetails)
            }
        }
        return userDetailsList

    }
    // invite user into an organization as an ADMIN user ROLE
}

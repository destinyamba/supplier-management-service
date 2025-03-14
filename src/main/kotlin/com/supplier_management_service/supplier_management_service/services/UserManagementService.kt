package com.supplier_management_service.supplier_management_service.services

import com.supplier_management_service.supplier_management_service.dtos.request.EditUserRequest
import com.supplier_management_service.supplier_management_service.models.*
import com.supplier_management_service.supplier_management_service.repositories.ClientRepository
import com.supplier_management_service.supplier_management_service.repositories.PendingUsersRepository
import com.supplier_management_service.supplier_management_service.repositories.SupplierRepository
import com.supplier_management_service.supplier_management_service.repositories.UserRepository
import org.slf4j.LoggerFactory
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class UserManagementService(
    private val userRepository: UserRepository,
    private val supplierRepository: SupplierRepository,
    private val clientRepository: ClientRepository,
    private val pendingUsersRepository: PendingUsersRepository,
    private val emailService: EmailService,
    private val passwordEncoder: PasswordEncoder,
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
    fun inviteUserInitial(pendingUser: PendingUser): PendingUser {
        // get org name from org id.
        val orgName = when (pendingUser.businessType) {
            BusinessType.CLIENT -> clientRepository.findClientById(pendingUser.orgId)?.clientName
            BusinessType.SUPPLIER -> supplierRepository.findSupplierById(pendingUser.orgId)?.supplierName
            null -> {
                logger.warn("Business type is null for pending user: ${pendingUser.email}")
                null
            }
        }

        // If orgName is still null, throw an error
        val finalOrgName = orgName ?: throw IllegalArgumentException("Organization not found for ID: ${pendingUser.orgId}")

        // save user details to pending Users collection
        val updatedPendingUser = pendingUser.copy(orgName = finalOrgName)
        val savedPendingUser = pendingUsersRepository.save(updatedPendingUser)
        logger.info("Saved pending user: ${savedPendingUser.email}")
        // send email with link to set password asynchronously via send grid
        try {
            emailService.sendSetPassword(pendingUser.email)
            logger.info("Sent password reset email to: ${pendingUser.email}")
        } catch (ex: EmailService.EmailSendException) {
            logger.error("Failed to send password reset email to ${pendingUser.email}: ${ex.message}")
            throw ex
        }

        val pendingUserDetails = PendingUser(
            email = pendingUser.email,
            name = pendingUser.name,
            role = pendingUser.role,
            orgId = pendingUser.orgId,
            orgName = finalOrgName,
            businessType = pendingUser.businessType,
            createdAt = pendingUser.createdAt,
            lastSignIn = pendingUser.lastSignIn,
        )

        return pendingUserDetails
    }

    fun inviteUserComplete(email: String, password: String): UserDetails? {
        // check that the email exists in the pending users collection
        val invitedUser = pendingUsersRepository.findUserByEmail(email)

        // save the password and other user details to the user repository
        val newUser = invitedUser.businessType?.let {
            User(
                id = invitedUser.id,
                email = invitedUser.email,
                password = passwordEncoder.encode(password), // assuming the User entity has a password field
                businessType = it,
                role = invitedUser.role,
                organizationName = invitedUser.orgName,
                orgId = invitedUser.orgId,
                name = invitedUser.name,
                lastSignIn = invitedUser.lastSignIn,
                createdAt = invitedUser.createdAt,
            )
        }
        userRepository.save(newUser!!)

        val savedUser = UserDetails(
            userId = newUser.id!!,
            email = newUser.email,
            userType = newUser.businessType.value,
            role = newUser.role,
            organizationName = newUser.organizationName,
            orgId = newUser.orgId,
            name = newUser.name,
            lastSignIn = newUser.lastSignIn,
            createdAt = newUser.createdAt,
        )

        // delete pending user
        pendingUsersRepository.delete(invitedUser)

        return savedUser
    }

    // delete user
    fun deleteUser(userId: String) {
        val user = userRepository.findById(userId).orElseThrow { IllegalArgumentException("User not found") }
        userRepository.delete(user)
        logger.info("Deleted user with email: $userId")
    }

    // edit user
    fun editUser(editUserRequest: EditUserRequest) {
        val user = userRepository.findById(editUserRequest.userId).orElseThrow { IllegalArgumentException("User not found") }

        editUserRequest.newName?.let { user.name = it }
        editUserRequest.newEmail?.let { user.email = it }
        editUserRequest.newRole?.let { user.role = it }

        userRepository.save(user)
        logger.info("Updated user with _id: ${editUserRequest.userId}")
    }

    // get user details by id
    fun getUserById(userId: String): UserDetails? {
        val user = userRepository.findById(userId).orElseThrow { IllegalArgumentException("User not found") }
        return user.id?.let {
            UserDetails(
                userId = it,
                email = user.email,
                name = user.name,
                role = user.role,
                userType = user.businessType.value,
                organizationName = user.organizationName,
                lastSignIn = user.lastSignIn,
                createdAt = user.createdAt,
                orgId = user.orgId
            )
        }
    }

}

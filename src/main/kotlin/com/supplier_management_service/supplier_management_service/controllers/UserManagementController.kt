package com.supplier_management_service.supplier_management_service.controllers

import com.supplier_management_service.supplier_management_service.dtos.request.EditUserRequest
import com.supplier_management_service.supplier_management_service.models.*
import com.supplier_management_service.supplier_management_service.services.UserManagementService
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@CrossOrigin(origins = ["http://localhost:3000"])
@RestController
@RequestMapping("/api/v1/user-details")
class UserManagementController(private val userManagementService: UserManagementService) {
    val logger = LoggerFactory.getLogger(UserManagementController::class.java)

    @CrossOrigin(origins = ["http://localhost:3000"])
    @PostMapping("/get-user-details")
    fun getUserDetails(
        @RequestBody userEmail: UserRequest
    ): ResponseEntity<UserDetails?> {
        return try {
            val response = userManagementService.getUserDetails(userEmail.userEmail)
            ResponseEntity.ok(response)
        } catch (e: IllegalArgumentException) {
            logger.error("Error user email not found: ${e.message}")
            ResponseEntity.status(HttpStatus.NOT_FOUND).body(null)
        } catch (e: Exception) {
            logger.error("Error occurred while fetching user details: ${e.message}")
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null)
        }
    }

    @GetMapping("/associated-org-users/{orgId}")
    fun getAssociatedOrgUsers(@PathVariable orgId: String): ResponseEntity<List<UserDetails?>> {
        return try {
            val response = userManagementService.usersUnderOrg(orgId)
            ResponseEntity.ok(response)
        } catch (e: IllegalArgumentException) {
            logger.error("Error: Org ID not found")
            ResponseEntity.status(HttpStatus.NOT_FOUND).body(null)
        } catch (e: Exception) {
            logger.error("Error fetching users associated with org. ${e.message}")
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null)
        }
    }

    @PostMapping("/invite-user-initial")
    fun inviteUserInitial(@RequestBody pendingUser: PendingUser): ResponseEntity<PendingUser> {
        return try {
            val userDetails = userManagementService.inviteUserInitial(pendingUser)
            ResponseEntity.ok(userDetails)
        } catch (e: Exception) {
            logger.error("Error inviting user: ${e.message}")
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null)
        }
    }

    @PostMapping("/invite-user-complete/{email}/{password}")
    fun inviteUserComplete(@PathVariable email: String, @PathVariable password: String): ResponseEntity<UserDetails> {
        return try {
            val userDetails = userManagementService.inviteUserComplete(email, password)
            ResponseEntity.ok(userDetails)
        } catch (e: Exception) {
            logger.error("Error setting user password: ${e.message}")
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null)
        }
    }

    @DeleteMapping("/delete/user/{userId}")
    fun deleteUser(@PathVariable userId: String): ResponseEntity<Void> {
        return try {
            userManagementService.deleteUser(userId)
            ResponseEntity.noContent().build()
        } catch (e: IllegalArgumentException) {
            logger.error("Error deleting user: ${e.message}")
            ResponseEntity.status(HttpStatus.NOT_FOUND).build()
        } catch (e: Exception) {
            logger.error("Error occurred while deleting user: ${e.message}")
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build()
        }
    }

    @PutMapping("/update/user")
    fun editUser(
        @RequestBody editUserRequest: EditUserRequest
    ): ResponseEntity<Void> {
        return try {
            userManagementService.editUser(editUserRequest)
            ResponseEntity.noContent().build()
        } catch (e: IllegalArgumentException) {
            logger.error("Error editing user: ${e.message}")
            ResponseEntity.status(HttpStatus.NOT_FOUND).build()
        } catch (e: Exception) {
            logger.error("Error occurred while editing user: ${e.message}")
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build()
        }
    }

    @GetMapping("/get-user-details/{userId}")
    fun getUserDetailsById(@PathVariable userId: String): ResponseEntity<UserDetails?> {
        return try {
            val response = userManagementService.getUserById(userId)
            ResponseEntity.ok(response)
        } catch (e: IllegalArgumentException) {
            logger.error("Error user id not found: ${e.message}")
            ResponseEntity.status(HttpStatus.NOT_FOUND).body(null)
        } catch (e: Exception) {
            logger.error("Error occurred while fetching user details by id: ${e.message}")
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null)
        }
    }

}

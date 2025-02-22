package com.supplier_management_service.supplier_management_service.controllers

import com.supplier_management_service.supplier_management_service.models.UserDetails
import com.supplier_management_service.supplier_management_service.models.UserRequest
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

}

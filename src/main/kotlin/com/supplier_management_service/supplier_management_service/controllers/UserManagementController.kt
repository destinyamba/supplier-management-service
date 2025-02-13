package com.supplier_management_service.supplier_management_service.controllers

import com.supplier_management_service.supplier_management_service.models.UserDetails
import com.supplier_management_service.supplier_management_service.models.UserRequest
import com.supplier_management_service.supplier_management_service.services.UserManagementService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@CrossOrigin(origins = ["http://localhost:3000"])
@RestController
@RequestMapping("/api/v1/user-details")
class UserManagementController(private val userManagementService: UserManagementService) {

    @CrossOrigin(origins = ["http://localhost:3000"])
    @PostMapping("/get-user-details")
    fun getUserDetails(
        @RequestBody userEmail: UserRequest
    ): ResponseEntity<UserDetails?> {
        return try {
            val response = userManagementService.getUserDetails(userEmail.userEmail)
            ResponseEntity.ok(response)
        } catch (e: Exception) {
            ResponseEntity(
                HttpStatus.INTERNAL_SERVER_ERROR
            )
        }
    }

}

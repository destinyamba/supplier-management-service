package com.supplier_management_service.supplier_management_service.controllers

import com.supplier_management_service.supplier_management_service.models.Auth0User
import com.supplier_management_service.supplier_management_service.services.UserManagementService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/users")
class UserManagementController(private val userManagementService: UserManagementService) {

    @GetMapping("/")
    fun getUsers(): ResponseEntity<List<Auth0User>> {
        return ResponseEntity.ok(userManagementService.getUsers())
    }

    @GetMapping("/public")
    fun getUser(): String {
        return userManagementService.getUser()
    }

    @GetMapping("/private")
    fun getProtectedUser(): String {
        return userManagementService.getProtectedUser()
    }

    @GetMapping("/admin")
    fun getAdminUser(): String {
        return userManagementService.getAdminUser()
    }

}

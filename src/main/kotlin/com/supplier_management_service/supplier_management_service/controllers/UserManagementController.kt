package com.supplier_management_service.supplier_management_service.controllers

import com.supplier_management_service.supplier_management_service.services.UserManagementService
import lombok.RequiredArgsConstructor
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/messages")
class UserManagementController(private val userManagementService: UserManagementService) {
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

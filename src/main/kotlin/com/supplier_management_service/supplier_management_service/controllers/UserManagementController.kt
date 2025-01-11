package com.supplier_management_service.supplier_management_service.controllers

import com.supplier_management_service.supplier_management_service.models.Auth0User
import com.supplier_management_service.supplier_management_service.services.PaginatedResponse
import com.supplier_management_service.supplier_management_service.services.UserManagementService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/users")
class UserManagementController(private val userManagementService: UserManagementService) {

    @GetMapping("/")
    fun getUsers(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") perPage: Int
    ): ResponseEntity<PaginatedResponse<Auth0User>> {
        val paginatedResponse = userManagementService.getUsers(page, perPage)
        return ResponseEntity.ok(paginatedResponse)
    }

}

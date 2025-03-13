package com.supplier_management_service.supplier_management_service.controllers

import com.supplier_management_service.supplier_management_service.models.ClientsDTO
import com.supplier_management_service.supplier_management_service.services.ClientOnboardingService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@CrossOrigin(origins = ["*"], maxAge = 3600)
@RestController
@RequestMapping("/api/v1/client")
class ClientController(
    private val clientOnboardingService: ClientOnboardingService
) {

    @PreAuthorize("hasRole('ADMIN') or hasRole('EDITOR')")
    @PostMapping("/onboard")
    fun onboardClient(@RequestBody clientData: ClientsDTO): ResponseEntity<ClientsDTO> {
        return try {
            val client = clientOnboardingService.onboardClient(clientData)
            ResponseEntity.ok(client)
        } catch (e: RuntimeException) {
            ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR)
        }
    }
}

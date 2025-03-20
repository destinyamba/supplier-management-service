package com.supplier_management_service.supplier_management_service.controllers

import com.sendgrid.rest.api.v3.integrations.models.Forbidden
import com.supplier_management_service.supplier_management_service.models.ClientsDTO
import com.supplier_management_service.supplier_management_service.services.ClientService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@CrossOrigin(origins = ["*"], maxAge = 3600)
@RestController
@RequestMapping("/api/v1/client")
class ClientController(
    private val clientService: ClientService
) {

    @PreAuthorize("hasRole('ADMIN') or hasRole('EDITOR')")
    @PostMapping("/onboard")
    fun onboardClient(@RequestBody clientData: ClientsDTO): ResponseEntity<ClientsDTO> {
        return try {
            val client = clientService.onboardClient(clientData)
            ResponseEntity.ok(client)
        } catch (e: RuntimeException) {
            ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR)
        }
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('EDITOR')")
    @PostMapping("/{clientId}/approve/{supplierId}")
    fun addApprovedSupplier(@PathVariable clientId: String, @PathVariable supplierId: String): ResponseEntity<ClientsDTO?> {
        return try {
            val client = clientService.addSupplierToList(clientId, supplierId)
            ResponseEntity.ok(client)
        } catch (e: RuntimeException) {
            ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR)
        }
    }

}

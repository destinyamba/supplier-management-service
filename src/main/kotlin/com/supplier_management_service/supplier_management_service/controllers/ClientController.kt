package com.supplier_management_service.supplier_management_service.controllers

import com.supplier_management_service.supplier_management_service.dtos.response.PagedResponse
import com.supplier_management_service.supplier_management_service.dtos.response.SupplierResponse
import com.supplier_management_service.supplier_management_service.models.ClientsDTO
import com.supplier_management_service.supplier_management_service.models.ContractType
import com.supplier_management_service.supplier_management_service.services.ClientService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.util.*

@CrossOrigin(origins = ["http://localhost:3000"])
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
    @PostMapping("/approve/supplier")
    fun addApprovedSupplier(
        @RequestParam clientId: String,
        @RequestParam supplierId: String,
        @RequestParam contractType: String
    ): ResponseEntity<Any> {
        return try {
            val contractTypeEnum = ContractType.valueOf(contractType.uppercase(Locale.getDefault()))
            val client = clientService.addSupplierToList(clientId, supplierId, contractTypeEnum)
            ResponseEntity.ok(client)
        } catch (e: IllegalArgumentException) {
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body(mapOf("error" to "Invalid contract type: $contractType"))
        } catch (e: RuntimeException) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(mapOf("error" to e.message))
        }
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('EDITOR')")
    @GetMapping("/{clientId}/suppliers")
    fun getApprovedSuppliers(
        @PathVariable clientId: String,
        @RequestParam(defaultValue = "1") page: Int,
        @RequestParam(defaultValue = "12") pageSize: Int
    ): ResponseEntity<PagedResponse<SupplierResponse>> {
        return try {
            val pagedResponse = clientService.approvedSuppliers(clientId, page, pageSize)
            ResponseEntity.ok(pagedResponse)
        } catch (e: RuntimeException) {
            ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR)
        }
    }

}
